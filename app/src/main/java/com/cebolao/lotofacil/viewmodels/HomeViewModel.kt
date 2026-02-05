package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val historyRepository: HistoryRepository,
    private val statisticsAnalyzer: StatisticsAnalyzer,
    private val statisticsRepository: StatisticsRepository,
    private val dispatchersProvider: DispatchersProvider
) : StateViewModel<HomeUiState>(HomeUiState()) {

    // Track flow collection jobs to prevent subscription leaks
    private var syncStatusJob: Job? = null

    init {
        observeSyncStatus()
        loadInitialData()
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel all flow collection jobs to prevent memory leaks
        syncStatusJob?.cancel()
        statsJob?.cancel()
    }

    private fun observeSyncStatus() {
        // Use stateIn with proper lifecycle management instead of launchIn
        syncStatusJob = viewModelScope.launch {
            historyRepository.syncStatus.collect { status ->
                try {
                    if (status is SyncStatus.Failed) {
                        val messageResId = R.string.error_sync_failed
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = messageResId))
                    }
                } catch (e: Exception) {
                    // Log exception but don't crash the app
                    val error = ErrorMapper.toAppError(e)
                    val userFriendlyMessage = ErrorMapper.messageFor(error)
                    sendUiEvent(UiEvent.ShowSnackbar(message = userFriendlyMessage))
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
                        // Only show snackbar if it's not just pending initialization
                        // But for now we stick to showing it.
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
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
            try {
                updateState { it.copy(isRefreshing = true, errorMessageResId = null) }
                val result = historyRepository.syncHistory()
                updateState { it.copy(isRefreshing = false) }
                when (result) {
                    is AppResult.Success -> sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.refresh_success))
                    is AppResult.Failure -> {
                        val messageResId = R.string.error_sync_failed
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = messageResId))
                    }
                }
            } catch (e: Exception) {
                updateState { it.copy(isRefreshing = false) }
                val error = ErrorMapper.toAppError(e)
                val messageResId = R.string.error_sync_failed
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = messageResId))
            }
        }
    }

    private var statsJob: kotlinx.coroutines.Job? = null

    fun onTimeWindowSelected(window: Int) {
        val current = currentState
        if (current.selectedTimeWindow == window) return
        
        // Check persistent cache first
        statsJob?.cancel()
        statsJob = viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isStatsLoading = true, selectedTimeWindow = window) }
            
            try {
                // Check persistent cache first
                val cachedStats = statisticsRepository.getCachedStatistics(window)
                if (cachedStats != null) {
                    updateState { it.copy(statistics = cachedStats, isStatsLoading = false) }
                    return@launch
                }
                
                // Use local history flow or current state history access if available, 
                // but repository flow is safer source of truth.
                val allHistory = historyRepository.getHistory().first()
                val draws = if (window > 0) allHistory.take(window) else allHistory
                val newStats = statisticsAnalyzer.analyze(draws)
                
                // Cache persistently with TTL
                statisticsRepository.cacheStatistics(window, newStats)
                
                updateState { it.copy(statistics = newStats, isStatsLoading = false) }
            } catch (e: Exception) {
                updateState { it.copy(isStatsLoading = false) }
                // Handle error appropriately
                val error = ErrorMapper.toAppError(e)
                val userFriendlyMessage = ErrorMapper.messageFor(error)
                sendUiEvent(UiEvent.ShowSnackbar(message = userFriendlyMessage))
            }
        }
    }

    fun onPatternSelected(pattern: StatisticPattern) {
        val current = currentState
        if (current.selectedPattern == pattern) return
        updateState { it.copy(selectedPattern = pattern) }
    }
}

/**
 * Events emitted by [HomeViewModel].
 */

