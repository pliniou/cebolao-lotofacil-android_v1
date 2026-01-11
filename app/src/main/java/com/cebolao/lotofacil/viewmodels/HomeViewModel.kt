package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.data.HistoricalDraw
import com.cebolao.lotofacil.di.DefaultDispatcher
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Removed embedded definitions of StatisticPattern, LastDrawStats and HomeUiState.
    // These types are now defined in HomeScreenModels.kt and the domain layer.

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val historyRepository: HistoryRepository,
    private val statisticsAnalyzer: StatisticsAnalyzer,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var fullHistory: List<HistoricalDraw> = emptyList()
    private var analysisJob: Job? = null

    init {
        observeSyncStatus()
        loadInitialData()
    }

    private fun observeSyncStatus() {
        viewModelScope.launch(dispatcher) {
            historyRepository.syncStatus.collect { status ->
                if (status is SyncStatus.Failed) {
                    _uiState.update { it.copy(showSyncFailedMessage = true) }
                }
            }
        }
    }

    fun onSyncMessageShown() {
        _uiState.update { it.copy(showSyncFailedMessage = false) }
    }

    fun retryInitialLoad() = loadInitialData()

    private fun loadInitialData() = viewModelScope.launch(dispatcher) {
        _uiState.update { it.copy(isScreenLoading = true, errorMessageResId = null) }
        getHomeScreenDataUseCase().collect { result ->
            result.onSuccess { data ->
                fullHistory = historyRepository.getHistory()
                _uiState.update {
                    it.copy(
                        isScreenLoading = false,
                        lastDrawStats = data.lastDrawStats,
                        statistics = data.initialStats
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isScreenLoading = false,
                        errorMessageResId = R.string.error_load_data_failed
                    )
                }
            }
        }
    }

    fun onTimeWindowSelected(window: Int) {
        if (_uiState.value.selectedTimeWindow == window) return
        analysisJob?.cancel()
        analysisJob = viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isStatsLoading = true, selectedTimeWindow = window) }
            val drawsToAnalyze = if (window > 0) fullHistory.take(window) else fullHistory
            val newStats = statisticsAnalyzer.analyze(drawsToAnalyze)
            _uiState.update { it.copy(statistics = newStats, isStatsLoading = false) }
        }
    }

    fun onPatternSelected(pattern: StatisticPattern) {
        _uiState.update { it.copy(selectedPattern = pattern) }
    }
}