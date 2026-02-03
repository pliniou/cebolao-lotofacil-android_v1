package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

@Stable
data class FiltersUiState(
    val filterStates: List<FilterState> = emptyList(),
    val isGenerating: Boolean = false,
    val lastDraw: Set<Int>? = null,
    val activeFiltersCount: Int = 0,
    val successProbability: Float = 1f,
    val generationState: GenerationUiState = GenerationUiState.Idle
)


@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val generateGamesUseCase: GenerateGamesUseCase,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FiltersUiState())
    val uiState: StateFlow<FiltersUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()



    init {
        loadLastDraw()
    }

    private fun loadLastDraw() {
        viewModelScope.launch {
            val lastDrawNumbers = historyRepository.getLastDraw()?.numbers
            _uiState.update { state ->
                state.copy(
                    lastDraw = lastDrawNumbers,
                    filterStates = FilterType.entries.map { FilterState(type = it) }
                )
            }
            updateProbability()
        }
    }

    fun onFilterToggle(type: FilterType, isEnabled: Boolean) {
        _uiState.update { state ->
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
        _uiState.update { state ->
            state.copy(
                filterStates = state.filterStates.map { f ->
                    if (f.type == type) f.copy(selectedRange = snappedRange) else f
                }
            )
        }
        updateProbability()
    }

    private fun updateProbability() {
        _uiState.update { state ->
            val activeFilters = state.filterStates.filter { it.isEnabled }
            state.copy(
                activeFiltersCount = activeFilters.size,
                successProbability = calculateSuccessProbability(activeFilters)
            )
        }
    }

    fun generateGames(quantity: Int) {
        if (_uiState.value.isGenerating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, generationState = GenerationUiState.Loading) }
            when (val result = generateGamesUseCase(quantity, _uiState.value.filterStates)) {
                is AppResult.Success -> {
                    gameRepository.addGeneratedGames(result.value)
                    _uiState.update { it.copy(generationState = GenerationUiState.Success(result.value.size)) }
                    _uiEvent.send(UiEvent.NavigateToGeneratedGames)
                }
                is AppResult.Failure -> {
                    val message = when (result.error) {
                        is DomainError.HistoryUnavailable -> "Histórico indisponível."
                        is DomainError.InvalidOperation -> result.error.reason
                        is DomainError.ValidationError -> result.error.message
                        else -> "Erro desconhecido ao gerar jogos."
                    }
                    _uiState.update { it.copy(generationState = GenerationUiState.Error(message)) }
                    _uiEvent.send(UiEvent.ShowSnackbar(message = message))
                }
            }
            _uiState.update { it.copy(isGenerating = false) }
        }
    }

    fun resetFilters() {
        _uiState.update { state ->
            state.copy(
                filterStates = FilterType.entries.map { FilterState(type = it) },
                activeFiltersCount = 0,
                successProbability = 1f
            )
        }
    }

    fun requestResetAllFilters() {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.ShowResetConfirmation)
        }
    }

    fun confirmResetAllFilters() {
        resetFilters()
    }

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
