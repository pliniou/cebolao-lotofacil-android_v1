package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.data.FilterState
import com.cebolao.lotofacil.data.FilterType
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

@Stable
data class FiltersScreenState(
    val filterStates: List<FilterState> = emptyList(),
    val generationState: GenerationUiState = GenerationUiState.Idle,
    val lastDraw: Set<Int>? = null,
    val activeFiltersCount: Int = 0,
    val successProbability: Float = 1f
)

@Stable
sealed interface GenerationUiState {
    object Idle : GenerationUiState
    data class Loading(val message: String) : GenerationUiState
}

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val generateGamesUseCase: GenerateGamesUseCase,
    historyRepository: HistoryRepository
) : BaseViewModel() {

    private val _filterStates = MutableStateFlow(FilterType.entries.map { FilterState(type = it) })
    private val _generationState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    private val _lastDraw = MutableStateFlow<Set<Int>?>(null)

    val uiState = combine(
        _filterStates, _generationState, _lastDraw
    ) { filters, generation, lastDraw ->
        val activeFilters = filters.filter { it.isEnabled }
        FiltersScreenState(
            filterStates = filters,
            generationState = generation,
            lastDraw = lastDraw,
            activeFiltersCount = activeFilters.size,
            successProbability = calculateSuccessProbability(activeFilters)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FiltersScreenState())

    init {
        viewModelScope.launch {
            _lastDraw.value = historyRepository.getLastDraw()?.numbers
        }
    }

    fun onFilterToggle(type: FilterType, isEnabled: Boolean) =
        _filterStates.update { it.map { f -> if (f.type == type) f.copy(isEnabled = isEnabled) else f } }

    fun onRangeAdjust(type: FilterType, newRange: ClosedFloatingPointRange<Float>) {
        val roundedStart = newRange.start.roundToInt().toFloat()
        val roundedEnd = newRange.endInclusive.roundToInt().toFloat()
        val snappedRange = roundedStart..roundedEnd

        _filterStates.update { currentStates ->
            currentStates.map { filterState ->
                if (filterState.type == type && filterState.selectedRange != snappedRange) {
                    filterState.copy(selectedRange = snappedRange)
                } else {
                    filterState
                }
            }
        }
    }

    fun generateGames(quantity: Int) {
        val currentState = _generationState.value
        if (currentState is GenerationUiState.Loading) return
        
        viewModelScope.launch {
            _generationState.value = GenerationUiState.Loading("Criando jogos com base nos seus filtros...")
            
            try {
                generateGamesUseCase(quantity, uiState.value.filterStates)
                    .onSuccess { games ->
                        gameRepository.addGeneratedGames(games)
                        navigateToGeneratedGames()
                    }
                    .onFailure { e ->
                        showSnackbar(e.message ?: "Erro desconhecido")
                    }
            } catch (e: Exception) {
                showSnackbar(e.message ?: "Erro ao gerar jogos")
            } finally {
                _generationState.value = GenerationUiState.Idle
            }
        }
    }

    fun resetAllFilters() {
        _filterStates.value = FilterType.entries.map { FilterState(type = it) }
    }

    fun requestResetAllFilters() {
        showResetConfirmation()
    }

    fun confirmResetAllFilters() {
        resetAllFilters()
    }

    private fun calculateSuccessProbability(activeFilters: List<FilterState>): Float {
        if (activeFilters.isEmpty()) return 1f
        val probability = activeFilters.fold(1.0) { acc, filter ->
            val rangeCoverage = filter.rangePercentage
            val filterSuccessChance = filter.type.historicalSuccessRate.pow(1 - rangeCoverage)
            acc * filterSuccessChance
        }
        return probability.toFloat().coerceIn(0f, 1f)
    }
}
