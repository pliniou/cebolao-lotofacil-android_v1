package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val historyRepository: HistoryRepository,
    private val statisticsAnalyzer: StatisticsAnalyzer,
    private val dispatchersProvider: DispatchersProvider
) : StateViewModel<HomeUiState>(HomeUiState()) {

    private var fullHistory: List<HistoricalDraw> = emptyList()
    private var analysisJob: Job? = null

    init {
        observeSyncStatus()
        loadInitialData()
    }

    private fun observeSyncStatus() {
        viewModelScope.launch(dispatchersProvider.default) {
            historyRepository.syncStatus.collect { status ->
                if (status is SyncStatus.Failed) {
                    showSnackbar(R.string.sync_failed_message)
                }
            }
        }
    }

    fun retryInitialLoad() = loadInitialData()

    fun refreshData() {
        viewModelScope.launch(dispatchersProvider.default) {
            updateState { it.copy(isScreenLoading = true, errorMessageResId = null) }
            getHomeScreenDataUseCase().collect { result ->
                result.onSuccess { data ->
                    fullHistory = data.history
                    val lastHistoryDate = data.history.firstOrNull()?.date
                    updateState {
                        it.copy(
                            isScreenLoading = false,
                            lastDrawStats = data.lastDrawStats,
                            statistics = data.initialStats,
                            lastUpdateTime = lastHistoryDate
                        )
                    }
                    showSnackbar(R.string.refresh_success)
                }
                result.onFailure {
                    updateState {
                        it.copy(
                            isScreenLoading = false,
                            errorMessageResId = R.string.refresh_error
                        )
                    }
                    showSnackbar(R.string.refresh_error)
                }
            }
        }
    }

    private fun loadInitialData() = viewModelScope.launch(dispatchersProvider.default) {
        updateState { it.copy(isScreenLoading = true, errorMessageResId = null) }
        getHomeScreenDataUseCase().collect { result ->
            result.onSuccess { data ->
                fullHistory = data.history
                updateState {
                    it.copy(
                        isScreenLoading = false,
                        lastDrawStats = data.lastDrawStats,
                        statistics = data.initialStats,
                        lastUpdateTime = data.history.firstOrNull()?.date
                    )
                }
            }.onFailure {
                updateState {
                    it.copy(
                        isScreenLoading = false,
                        errorMessageResId = R.string.error_load_data_failed
                    )
                }
            }
        }
    }

    fun onTimeWindowSelected(window: Int) {
        if (currentState.selectedTimeWindow == window) return
        
        analysisJob?.cancel()
        analysisJob = viewModelScope.launch(dispatchersProvider.default) {
            updateState { 
                it.copy(
                    isStatsLoading = true, 
                    selectedTimeWindow = window,
                    statistics = if (window == 0 && currentState.selectedTimeWindow != 0) {
                        // Reset to initial stats when selecting "all time"
                        currentState.statistics?.copy()
                    } else {
                        currentState.statistics
                    }
                ) 
            }
            
            val drawsToAnalyze = if (window > 0) fullHistory.take(window) else fullHistory
            val newStats = statisticsAnalyzer.analyze(drawsToAnalyze)
            
            updateState { 
                it.copy(
                    statistics = newStats, 
                    isStatsLoading = false
                )
            }
        }
    }

    fun onPatternSelected(pattern: StatisticPattern) {
        if (currentState.selectedPattern == pattern) return
        updateState { it.copy(selectedPattern = pattern) }
    }
}
