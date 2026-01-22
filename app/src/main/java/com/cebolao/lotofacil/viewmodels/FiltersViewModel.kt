package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val historyRepository: HistoryRepository
) : StateViewModel<FiltersScreenState>(FiltersScreenState()) {

    init {
        loadLastDraw()
    }

    private fun loadLastDraw() {
        viewModelScope.launch {
            val lastDrawNumbers = historyRepository.getLastDraw()?.numbers
            updateState { state ->
                state.copy(
                    lastDraw = lastDrawNumbers,
                    filterStates = FilterType.entries.map { FilterState(type = it) }
                )
            }
            updateProbability()
        }
    }

    fun onFilterToggle(type: FilterType, isEnabled: Boolean) {
        updateState { state ->
            state.copy(
                filterStates = state.filterStates.map { f ->
                    if (f.type == type) f.copy(isEnabled = isEnabled) else f
                }
            )
        }
        updateProbability()
    }

    fun onRangeAdjust(type: FilterType, newRange: ClosedFloatingPointRange<Float>) {
        val snappedRange = newRange.start.roundToInt().toFloat()..newRange.endInclusive.roundToInt().toFloat()
        
        updateState { state ->
            state.copy(
                filterStates = state.filterStates.map { f ->
                    if (f.type == type) f.copy(selectedRange = snappedRange) else f
                }
            )
        }
        updateProbability()
    }

    private fun updateProbability() {
        updateState { state ->
            val activeFilters = state.filterStates.filter { it.isEnabled }
            state.copy(
                activeFiltersCount = activeFilters.size,
                successProbability = calculateSuccessProbability(activeFilters)
            )
        }
    }

    fun generateGames(quantity: Int) {
        if (currentState.generationState is GenerationUiState.Loading) return
        
        viewModelScope.launch {
            updateState { it.copy(generationState = GenerationUiState.Loading("Criando jogos...")) }
            
            try {
                generateGamesUseCase(quantity, currentState.filterStates)
                    .onSuccess { games ->
                        gameRepository.addGeneratedGames(games)
                        navigateToGeneratedGames()
                    }
                    .onFailure { e ->
                        showSnackbar(e.message ?: "Erro ao gerar jogos")
                    }
            } catch (e: Exception) {
                showSnackbar(e.message ?: "Erro inesperado")
            } finally {
                updateState { it.copy(generationState = GenerationUiState.Idle) }
            }
        }
    }

    fun resetAllFilters() {
        updateState { state ->
            state.copy(
                filterStates = FilterType.entries.map { FilterState(type = it) },
                activeFiltersCount = 0,
                successProbability = 1f
            )
        }
    }

    fun requestResetAllFilters() = showResetConfirmation()

    fun confirmResetAllFilters() = resetAllFilters()

    private fun calculateSuccessProbability(activeFilters: List<FilterState>): Float {
        if (activeFilters.isEmpty()) return 1f
        val probability = activeFilters.fold(1.0) { acc, filter ->
            val rangeCoverage = filter.rangePercentage
            val filterSuccessChance = filter.type.historicalSuccessRate.toDouble().pow(1.0 - rangeCoverage)
            acc * filterSuccessChance
        }
        return probability.toFloat().coerceIn(0f, 1f)
    }
}
