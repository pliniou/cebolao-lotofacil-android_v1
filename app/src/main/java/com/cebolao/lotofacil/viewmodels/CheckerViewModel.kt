package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class CheckerScreenState(
    val uiState: CheckerUiState = CheckerUiState.Idle,
    val selectedNumbers: Set<Int> = emptySet(),
    val showClearConfirmation: Boolean = false,
    val showClearResultsConfirmation: Boolean = false
)

@Stable
sealed interface CheckerUiState {
    data object Idle : CheckerUiState
    data class Loading(val progress: Float = 0f, val message: String = "Analisando...") : CheckerUiState
    data class Success(
        val result: CheckResult,
        val simpleStats: ImmutableList<Pair<String, String>>
    ) : CheckerUiState
    data class Error(val messageResId: Int, val canRetry: Boolean = true) : CheckerUiState
}

@HiltViewModel
class CheckerViewModel @Inject constructor(
    private val checkGameUseCase: CheckGameUseCase,
    savedStateHandle: SavedStateHandle
) : StateViewModel<CheckerScreenState>(CheckerScreenState()) {

    private var checkJob: Job? = null
    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        savedStateHandle.get<String?>(Destination.Checker.NUMBERS_ARG)?.let { arg ->
            val numbers = arg.split(',').mapNotNull { it.toIntOrNull() }.toSet()
            if (numbers.isNotEmpty()) {
                updateState { it.copy(selectedNumbers = numbers) }
                if (numbers.size == LotofacilConstants.GAME_SIZE) {
                    onCheckGameClicked()
                }
            }
        }
    }

    fun onNumberClicked(number: Int) {
        if (currentState.uiState is CheckerUiState.Loading) return
        
        updateState { state ->
            val newSelection = state.selectedNumbers.toMutableSet()
            if (number in newSelection) {
                newSelection.remove(number)
            } else if (newSelection.size < LotofacilConstants.GAME_SIZE) {
                newSelection.add(number)
            }
            state.copy(
                selectedNumbers = newSelection,
                uiState = CheckerUiState.Idle
            )
        }
    }

    fun onCheckGameClicked() {
        val selection = currentState.selectedNumbers
        if (selection.size != LotofacilConstants.GAME_SIZE) {
            updateState { it.copy(uiState = CheckerUiState.Error(R.string.checker_incomplete_selection, canRetry = false)) }
            return
        }
        
        // Show confirmation if there are existing results
        if (currentState.uiState is CheckerUiState.Success) {
            updateState { it.copy(showClearResultsConfirmation = true) }
        } else {
            checkGame(selection)
        }
    }

    fun confirmClearResults() {
        updateState { it.copy(showClearResultsConfirmation = false) }
        checkGame(currentState.selectedNumbers)
    }

    fun dismissClearResultsConfirmation() {
        updateState { it.copy(showClearResultsConfirmation = false) }
    }

    private fun checkGame(numbers: Set<Int>) {
        checkJob?.cancel()
        checkJob = checkGameUseCase(numbers)
            .onEach { state -> 
                updateState { it.copy(uiState = state) }
                when (state) {
                    is CheckerUiState.Success -> {
                        _uiEvents.emit(UiEvent.ShowSnackbar(messageResId = R.string.checker_checking_success))
                    }
                    is CheckerUiState.Error -> {
                        _uiEvents.emit(UiEvent.ShowSnackbar(messageResId = R.string.checker_checking_error))
                    }
                    else -> { /* No feedback for loading states */ }
                }
            }
            .catch { _ ->
                updateState { it.copy(uiState = CheckerUiState.Error(R.string.error_unknown, canRetry = true)) }
                _uiEvents.emit(UiEvent.ShowSnackbar(messageResId = R.string.checker_checking_error))
            }
            .launchIn(viewModelScope)
    }

    fun onClearSelectionClicked() {
        if (currentState.selectedNumbers.isNotEmpty()) {
            updateState { it.copy(showClearConfirmation = true) }
        }
    }

    fun confirmClearSelection() {
        checkJob?.cancel()
        updateState { CheckerScreenState() }
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowSnackbar(messageResId = R.string.checker_clear_confirmation))
        }
    }

    fun dismissClearConfirmation() {
        updateState { it.copy(showClearConfirmation = false) }
    }
}

