package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
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
import com.cebolao.lotofacil.viewmodels.CheckerUiState

sealed interface GameUiEvent {
    data object ShowClearGamesDialog : GameUiEvent
    data object HideClearGamesDialog : GameUiEvent
    data class ShowDeleteGameDialog(val game: LotofacilGame) : GameUiEvent
    data object HideDeleteGameDialog : GameUiEvent
    data class ShowAnalysisDialog(val result: GameAnalysisResult) : GameUiEvent
    data object HideAnalysisDialog : GameUiEvent
}

@Stable
data class GameScreenUiState(
    val isLoading: Boolean = false,
    val showClearGamesDialog: Boolean = false,
    val gameToDelete: LotofacilGame? = null,
    val analysisResult: GameAnalysisResult? = null,
    val analysisState: GameAnalysisUiState = GameAnalysisUiState.Idle
)

@Stable
data class GameAnalysisResult(
    val game: LotofacilGame,
    val simpleStats: ImmutableList<Pair<String, String>>,
    val checkResult: CheckResult
)

@Stable
sealed interface GameAnalysisUiState {
    data object Idle : GameAnalysisUiState
    data object Loading : GameAnalysisUiState
    data class Success(val result: GameAnalysisResult) : GameAnalysisUiState
    data class Error(@StringRes val messageResId: Int) : GameAnalysisUiState
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val checkGameUseCase: CheckGameUseCase
) : StateViewModel<GameScreenUiState>(GameScreenUiState()) {

    val generatedGames: StateFlow<ImmutableList<LotofacilGame>> = gameRepository.games
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = persistentListOf()
        )

    fun clearUnpinned() = updateState { it.copy(showClearGamesDialog = true) }

    fun confirmClearUnpinned() {
        viewModelScope.launch {
            gameRepository.clearUnpinnedGames()
            updateState { it.copy(showClearGamesDialog = false) }
            showSnackbar(R.string.unpinned_games_cleared)
        }
    }

    fun dismissClearDialog() = updateState { it.copy(showClearGamesDialog = false) }

    fun togglePinState(gameToToggle: LotofacilGame) = viewModelScope.launch {
        gameRepository.togglePinState(gameToToggle)
    }

    fun requestDeleteGame(game: LotofacilGame) = updateState { it.copy(gameToDelete = game) }

    fun confirmDeleteGame(game: LotofacilGame) {
        viewModelScope.launch {
            gameRepository.deleteGame(game)
            updateState { it.copy(gameToDelete = null) }
            showSnackbar(R.string.game_deleted_confirmation)
        }
    }

    fun dismissDeleteDialog() = updateState { it.copy(gameToDelete = null) }

    fun analyzeGame(game: LotofacilGame) {
        if (currentState.analysisState is GameAnalysisUiState.Loading) return
        
        viewModelScope.launch {
            updateState { it.copy(analysisState = GameAnalysisUiState.Loading) }
            try {
                val checkUiState = checkGameUseCase(game.numbers)
                    .first { it is CheckerUiState.Success || it is CheckerUiState.Error }

                if (checkUiState is CheckerUiState.Success) {
                    val result = GameAnalysisResult(
                        game = game,
                        simpleStats = checkUiState.simpleStats,
                        checkResult = checkUiState.result
                    )
                    updateState { it.copy(
                        analysisState = GameAnalysisUiState.Success(result),
                        analysisResult = result
                    ) }
                } else {
                    val errorResId = (checkUiState as? CheckerUiState.Error)?.messageResId ?: R.string.error_analysis_failed
                    updateState { it.copy(analysisState = GameAnalysisUiState.Error(errorResId)) }
                }
            } catch (_: Exception) {
                updateState { it.copy(analysisState = GameAnalysisUiState.Error(R.string.error_analysis_failed)) }
            }
        }
    }

    fun dismissAnalysisDialog() = updateState { 
        it.copy(
            analysisResult = null,
            analysisState = GameAnalysisUiState.Idle
        )
    }
}

