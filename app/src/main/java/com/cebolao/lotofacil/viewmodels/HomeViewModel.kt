package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.StringRes
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val historyRepository: HistoryRepository,
    private val statisticsAnalyzer: StatisticsAnalyzer,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events

    init {
        observeSyncStatus()
        loadInitialData()
    }

    private fun observeSyncStatus() {
        historyRepository.syncStatus.onEach { status ->
            if (status is SyncStatus.Failed) {
                _uiEvent.send(UiEvent.ShowSnackbar(message = "Sincronização falhou. Verifique sua conexão."))
            }
        }.launchIn(viewModelScope)
    }

    private fun loadInitialData() {
        viewModelScope.launch(dispatchersProvider.default) {
            _uiState.update { it.copy(isScreenLoading = true, errorMessageResId = null) }
            getHomeScreenDataUseCase().collect { result ->
                when (result) {
                    is com.cebolao.lotofacil.core.result.AppResult.Success -> {
                        val data = result.value
                        val lastDraw = data.history.firstOrNull()
                        val nextDrawStats = data.lastDrawStats
                        _uiState.update {
                            it.copy(
                                isScreenLoading = false,
                                lastDrawStats = data.lastDrawStats,
                                statistics = data.initialStats,
                                lastUpdateTime = lastDraw?.date,
                                nextDrawDate = nextDrawStats?.nextDate,
                                nextDrawContest = nextDrawStats?.nextContest,
                                isTodayDrawDay = checkIsTodayDrawDay(nextDrawStats?.nextDate)
                            )
                        }
                    }
                    is com.cebolao.lotofacil.core.result.AppResult.Failure -> {
                        _uiState.update {
                            it.copy(isScreenLoading = false, errorMessageResId = R.string.error_load_data_failed)
                        }
                        _uiEvent.send(UiEvent.ShowSnackbar(message = "Erro ao carregar dados."))
                    }
                }
            }
        }
    }

    private fun checkIsTodayDrawDay(nextDate: String?): Boolean {
        if (nextDate.isNullOrBlank()) return false
        return try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val today = dateFormat.format(java.util.Date())
            today == nextDate
        } catch (_: Exception) {
            false
        }
    }

    fun refreshData() {
        viewModelScope.launch(dispatchersProvider.default) {
            _uiState.update { it.copy(isRefreshing = true, errorMessageResId = null) }
            getHomeScreenDataUseCase().collect { result ->
                when (result) {
                    is com.cebolao.lotofacil.core.result.AppResult.Success -> {
                        val data = result.value
                        val nextDrawStats = data.lastDrawStats
                        _uiState.update {
                            it.copy(
                                isRefreshing = false,
                                lastDrawStats = data.lastDrawStats,
                                statistics = data.initialStats,
                                lastUpdateTime = data.history.firstOrNull()?.date,
                                nextDrawDate = nextDrawStats?.nextDate,
                                nextDrawContest = nextDrawStats?.nextContest,
                                isTodayDrawDay = checkIsTodayDrawDay(nextDrawStats?.nextDate)
                            )
                        }
                        _uiEvent.send(UiEvent.ShowSnackbar(message = "Dados atualizados com sucesso."))
                    }
                    is com.cebolao.lotofacil.core.result.AppResult.Failure -> {
                        _uiState.update {
                            it.copy(isRefreshing = false, errorMessageResId = R.string.refresh_error)
                        }
                        _uiEvent.send(UiEvent.ShowSnackbar(message = "Erro ao atualizar dados."))
                    }
                }
            }
        }
    }

    fun onTimeWindowSelected(window: Int) {
        val current = _uiState.value
        if (current.selectedTimeWindow == window) return
        viewModelScope.launch(dispatchersProvider.default) {
            _uiState.update { it.copy(isStatsLoading = true, selectedTimeWindow = window) }
            val draws = if (window > 0) historyRepository.getHistory().take(window) else historyRepository.getHistory()
            val newStats = statisticsAnalyzer.analyze(draws)
            _uiState.update { it.copy(statistics = newStats, isStatsLoading = false) }
        }
    }

    fun onPatternSelected(pattern: StatisticPattern) {
        val current = _uiState.value
        if (current.selectedPattern == pattern) return
        _uiState.update { it.copy(selectedPattern = pattern) }
    }
}

/**
 * Events emitted by [HomeViewModel].  Similar to other event classes,
 * these are collected by the UI to display snackbars or dialogs.
 */
sealed interface HomeEvent {
    data class ShowSnackbar(@StringRes val messageRes: Int) : HomeEvent
}
