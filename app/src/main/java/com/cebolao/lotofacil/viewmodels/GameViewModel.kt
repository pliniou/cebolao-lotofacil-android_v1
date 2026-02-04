package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import com.cebolao.lotofacil.domain.usecase.ClearUnpinnedGamesUseCase
import com.cebolao.lotofacil.domain.usecase.DeleteGameUseCase
import com.cebolao.lotofacil.domain.usecase.GameCheckState
import com.cebolao.lotofacil.domain.usecase.GetSavedGamesUseCase
import com.cebolao.lotofacil.domain.usecase.ToggleGamePinUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Events emitted by [GameViewModel] for navigation, dialogs and snackbars.
 */


@Stable
data class GameUiState(
    val isLoading: Boolean = false,
    val analysisState: GameAnalysisUiState = GameAnalysisUiState.Idle,
    val analysisResult: GameAnalysisResult? = null,
    val showClearGamesDialog: Boolean = false,
    val gameToDelete: LotofacilGame? = null
)

@Stable
data class GameAnalysisResult(
    val game: LotofacilGame,
    val simpleStats: ImmutableList<GameStatistic>,
    val checkResult: CheckResult
)

@Stable
sealed interface GameAnalysisUiState {
    data object Idle : GameAnalysisUiState
    data object Loading : GameAnalysisUiState
    data class Success(val gameCount: Int = 0) : GameAnalysisUiState
    data class Error(val messageResId: Int) : GameAnalysisUiState
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getSavedGamesUseCase: GetSavedGamesUseCase,
    private val checkGameUseCase: CheckGameUseCase,
    private val clearUnpinnedGamesUseCase: ClearUnpinnedGamesUseCase,
    private val toggleGamePinUseCase: ToggleGamePinUseCase,
    private val deleteGameUseCase: DeleteGameUseCase
) : StateViewModel<GameUiState>(GameUiState()) {

    val generatedGames: StateFlow<ImmutableList<LotofacilGame>> = getSavedGamesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = persistentListOf()
        )
    fun onClearGamesRequested() {
        viewModelScope.launch { updateState { it.copy(showClearGamesDialog = true) } }
    }
    fun confirmClearUnpinned() {
        viewModelScope.launch {
            clearUnpinnedGamesUseCase()
            updateState { it.copy(showClearGamesDialog = false) }
            sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.unpinned_games_cleared))
        }
    }
    fun dismissClearDialog() {
        updateState { it.copy(showClearGamesDialog = false) }
    }
    fun onDeleteGameRequested(game: LotofacilGame) {
        viewModelScope.launch { updateState { it.copy(gameToDelete = game) } }
    }
    fun confirmDeleteGame(game: LotofacilGame) {
        viewModelScope.launch {
            deleteGameUseCase(game)
            updateState { it.copy(gameToDelete = null) }
            sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.game_deleted_confirmation))
        }
    }
    fun dismissDeleteDialog() {
        updateState { it.copy(gameToDelete = null) }
    }
    fun analyzeGame(game: LotofacilGame) {
        if (currentState.analysisState is GameAnalysisUiState.Loading) return
        viewModelScope.launch {
            updateState { it.copy(analysisState = GameAnalysisUiState.Loading) }
            try {
                val checkState = checkGameUseCase(game.numbers)
                    .first { it is GameCheckState.Success || it is GameCheckState.Failure }
                when (checkState) {
                    is GameCheckState.Success -> {
                        val result = GameAnalysisResult(
                            game = game,
                            simpleStats = checkState.stats.toImmutableList(),
                            checkResult = checkState.result
                        )
                        updateState { it.copy(analysisState = GameAnalysisUiState.Success(), analysisResult = result) }
                    }
                    is GameCheckState.Failure -> {
                        val messageResId = when (checkState.error) {
                            is com.cebolao.lotofacil.core.error.EmptyHistoryError -> R.string.error_no_history
                            else -> R.string.error_analysis_failed
                        }
                        updateState { it.copy(analysisState = GameAnalysisUiState.Error(messageResId)) }
                    }
                    else -> Unit
                }
            } catch (_: Exception) {
                val messageResId = R.string.error_analysis_failed
                updateState { it.copy(analysisState = GameAnalysisUiState.Error(messageResId)) }
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = messageResId))
            }
        }
    }
    fun dismissAnalysisDialog() {
        updateState { it.copy(analysisResult = null, analysisState = GameAnalysisUiState.Idle) }
    }
    fun clearUnpinned() {
        viewModelScope.launch {
            clearUnpinnedGamesUseCase()
        }
    }
    fun togglePinState(game: LotofacilGame) {
        viewModelScope.launch {
            toggleGamePinUseCase(game)
        }
    }
    fun requestDeleteGame(game: LotofacilGame) {
        viewModelScope.launch {
            _uiState.update { it.copy(gameToDelete = game) }
        }
    }
}
