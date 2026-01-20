package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.di.DefaultDispatcher
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.viewmodels.HomeUiState
import androidx.compose.runtime.Stable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Removed embedded definitions of StatisticPattern, LastDrawStats and HomeUiState.
    // These types are now defined in HomeScreenModels.kt and the domain layer.

/**
 * Data class representing statistics chip values for UI display.
 */
@Stable
data class StatChipValues(
    val sum: String,
    val evens: String,
    val primes: String,
    val frame: String,
    val portrait: String,
    val fibonacci: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val historyRepository: HistoryRepository,
    private val statisticsAnalyzer: StatisticsAnalyzer,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : StateViewModel<HomeUiState>(HomeUiState()) {

    /**
     * Computed statistics chip values for UI display.
     */
    val statChipValues: StateFlow<StatChipValues?> = uiState.map { state ->
        state.lastDrawStats?.let { stats ->
            StatChipValues(
                sum = stats.sum.toString(),
                evens = stats.evens.toString(),
                primes = stats.primes.toString(),
                frame = stats.frame.toString(),
                portrait = stats.portrait.toString(),
                fibonacci = stats.fibonacci.toString()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

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
                    showSnackbar(R.string.sync_failed_message)
                }
            }
        }
    }

    fun retryInitialLoad() = loadInitialData()

    fun refreshData() {
        viewModelScope.launch(dispatcher) {
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

    private fun loadInitialData() = viewModelScope.launch(dispatcher) {
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
        analysisJob = viewModelScope.launch(dispatcher) {
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
