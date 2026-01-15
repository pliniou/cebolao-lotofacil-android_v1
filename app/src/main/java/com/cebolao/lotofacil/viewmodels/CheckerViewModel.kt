package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.data.CheckResult
import com.cebolao.lotofacil.data.LotofacilConstants
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import com.cebolao.lotofacil.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckerUiState>(CheckerUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _selectedNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val selectedNumbers = _selectedNumbers.asStateFlow()

    private var checkJob: Job? = null

    init {
        savedStateHandle.get<String?>(Destination.Checker.NUMBERS_ARG)?.let { arg ->
            val numbers = arg.split(',').mapNotNull { it.toIntOrNull() }.toSet()
            if (numbers.isNotEmpty()) {
                _selectedNumbers.value = numbers
                if (numbers.size == LotofacilConstants.GAME_SIZE) {
                    onCheckGameClicked()
                }
            }
        }
    }

    fun onNumberClicked(number: Int) {
        if (_uiState.value is CheckerUiState.Loading) return
        _uiState.value = CheckerUiState.Idle
        _selectedNumbers.update { currentSelection ->
            val newSelection = currentSelection.toMutableSet()
            if (number in newSelection) {
                newSelection.remove(number)
            } else if (newSelection.size < LotofacilConstants.GAME_SIZE) {
                newSelection.add(number)
            }
            newSelection
        }
    }

    fun onCheckGameClicked() {
        val selection = _selectedNumbers.value
        if (selection.size != LotofacilConstants.GAME_SIZE) {
            _uiState.value = CheckerUiState.Error(R.string.error_incomplete_selection, canRetry = false)
            return
        }
        checkGame(selection)
    }

    private fun checkGame(numbers: Set<Int>) {
        checkJob?.cancel()
        checkJob = checkGameUseCase(numbers)
            .onEach { state -> _uiState.value = state }
            .catch { _ ->
                _uiState.value = CheckerUiState.Error(R.string.error_unknown, canRetry = true)
            }
            .launchIn(viewModelScope)
    }

    fun clearSelection() {
        checkJob?.cancel()
        _selectedNumbers.value = emptySet()
        _uiState.value = CheckerUiState.Idle
    }
}