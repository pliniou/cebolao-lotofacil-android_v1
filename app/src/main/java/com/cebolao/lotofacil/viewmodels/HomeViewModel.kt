package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
<<<<<<< HEAD
import com.cebolao.lotofacil.core.result.AppResult
=======
>>>>>>> 16297bf53176bde23942576b6b1057afc7a219c9
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
import kotlinx.coroutines.flow.first
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
                _uiEvent.send(UiEvent.ShowSnackbar(messageResId = R.string.sync_failed_check_connection))
            }
        }.launchIn(viewModelScope)
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(isScreenLoading = true, errorMessageResId = null) }
        
        getHomeScreenDataUseCase()
            .onEach { result ->
                when (result) {
                    is AppResult.Success -> {
                        val data = result.value
                        val lastDraw = data.history.firstOrNull()
                        val nextDrawStats = data.lastDrawStats
                        _uiState.update {
                            it.copy(
                                isScreenLoading = false,
                                errorMessageResId = null,
                                lastDrawStats = data.lastDrawStats,
                                statistics = data.initialStats,
                                lastUpdateTime = lastDraw?.date,
                                nextDrawDate = nextDrawStats?.nextDate,
                                nextDrawContest = nextDrawStats?.nextContest,
                                isTodayDrawDay = checkIsTodayDrawDay(nextDrawStats?.nextDate)
                            )
                        }
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(isScreenLoading = false, errorMessageResId = R.string.error_load_data_failed)
                        }
                        // Only show snackbar if it's not just pending initialization
                        // But for now we stick to showing it.
                        _uiEvent.send(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkIsTodayDrawDay(nextDate: String?): Boolean {
        if (nextDate.isNullOrBlank()) return false
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val today = java.time.LocalDate.now().format(formatter)
            today == nextDate
        } catch (_: Exception) {
            false
        }
    }

    fun refreshData() {
        viewModelScope.launch(dispatchersProvider.default) {
            _uiState.update { it.copy(isRefreshing = true, errorMessageResId = null) }
            val result = historyRepository.syncHistory()
            _uiState.update { it.copy(isRefreshing = false) }
            when (result) {
                is AppResult.Success -> _uiEvent.send(UiEvent.ShowSnackbar(messageResId = R.string.refresh_success))
                is AppResult.Failure -> _uiEvent.send(UiEvent.ShowSnackbar(messageResId = R.string.refresh_error))
            }
        }
    }

    fun onTimeWindowSelected(window: Int) {
        val current = _uiState.value
        if (current.selectedTimeWindow == window) return
        viewModelScope.launch(dispatchersProvider.default) {
            _uiState.update { it.copy(isStatsLoading = true, selectedTimeWindow = window) }
            val allHistory = historyRepository.getHistory().first()
            val draws = if (window > 0) allHistory.take(window) else allHistory
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
 * Events emitted by [HomeViewModel].
 */
sealed interface HomeEvent {
    data class ShowSnackbar(@StringRes val messageRes: Int) : HomeEvent
}
