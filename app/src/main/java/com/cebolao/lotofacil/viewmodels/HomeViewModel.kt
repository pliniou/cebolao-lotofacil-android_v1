package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val historyRepository: HistoryRepository,
    private val statisticsAnalyzer: StatisticsAnalyzer,
    private val statisticsRepository: StatisticsRepository,
    private val dispatchersProvider: DispatchersProvider
) : StateViewModel<HomeUiState>(HomeUiState()) {

    private var syncStatusJob: Job? = null
    private var statsJob: Job? = null
    private var cachedHistory: List<HistoricalDraw> = emptyList()

    init {
        observeSyncStatus()
        loadInitialData()
    }

    override fun onCleared() {
        super.onCleared()
        syncStatusJob?.cancel()
        statsJob?.cancel()
    }

    private fun observeSyncStatus() {
        syncStatusJob = viewModelScope.launch {
            historyRepository.syncStatus.collect { status ->
                if (status is SyncStatus.Failed) {
                    sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_sync_failed))
                }
            }
        }
    }

    private fun loadInitialData() {
        updateState { it.copy(isScreenLoading = true, errorMessageResId = null) }
        
        getHomeScreenDataUseCase()
            .onEach { result ->
                when (result) {
                    is AppResult.Success -> {
                        val data = result.value
                        val lastDraw = data.history.firstOrNull()
                        cachedHistory = data.history

                        val nextDrawStats = data.lastDrawStats
                        updateState {
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
                        updateState {
                            it.copy(isScreenLoading = false, errorMessageResId = R.string.error_load_data_failed)
                        }
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkIsTodayDrawDay(nextDate: String?): Boolean {
        if (nextDate.isNullOrBlank()) return false
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val today = LocalDate.now().format(formatter)
            today == nextDate
        } catch (_: Exception) {
            false
        }
    }

    fun refreshData() {
        viewModelScope.launch(dispatchersProvider.default) {
            try {
                updateState { it.copy(isRefreshing = true, errorMessageResId = null) }
                val result = historyRepository.syncHistory()
                updateState { it.copy(isRefreshing = false) }
                when (result) {
                    is AppResult.Success -> {
                        statisticsRepository.clearCache()
                        val selectedWindow = currentState.selectedTimeWindow
                        if (selectedWindow > 0) {
                            onTimeWindowSelected(selectedWindow, forceRefresh = true)
                        }
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.refresh_success))
                    }
                    is AppResult.Failure -> {
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_sync_failed))
                    }
                }
            } catch (_: Exception) {
                updateState { it.copy(isRefreshing = false) }
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_sync_failed))
            }
        }
    }

    fun onTimeWindowSelected(window: Int, forceRefresh: Boolean = false) {
        val current = currentState
        if (!forceRefresh && current.selectedTimeWindow == window) return

        statsJob?.cancel()
        statsJob = viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isStatsLoading = true, selectedTimeWindow = window) }

            try {
                val cachedStats = if (forceRefresh) null else statisticsRepository.getCachedStatistics(window)
                if (cachedStats != null) {
                    updateState { it.copy(statistics = cachedStats, isStatsLoading = false) }
                    return@launch
                }

                val allHistory = cachedHistory.ifEmpty { historyRepository.getHistory().first() }
                val draws = if (window > 0) allHistory.take(window) else allHistory
                val newStats = statisticsAnalyzer.analyze(draws)

                statisticsRepository.cacheStatistics(window, newStats)
                updateState { it.copy(statistics = newStats, isStatsLoading = false) }
            } catch (_: Exception) {
                updateState { it.copy(isStatsLoading = false) }
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
            }
        }
    }

    fun onPatternSelected(pattern: StatisticPattern) {
        val current = currentState
        if (current.selectedPattern == pattern) return
        updateState { it.copy(selectedPattern = pattern) }
    }
}

