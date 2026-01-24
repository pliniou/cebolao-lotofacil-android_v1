package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import com.cebolao.lotofacil.domain.usecase.GameCheckState
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
sealed interface GameEvent {
    data object ShowClearDialog : GameEvent
    data class ShowDeleteDialog(val game: LotofacilGame) : GameEvent
    data class ShowAnalysisDialog(val result: GameAnalysisResult) : GameEvent
    data class ShowSnackbar(@StringRes val messageRes: Int) : GameEvent
}

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
    private val gameRepository: GameRepository,
    private val checkGameUseCase: CheckGameUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events
    val generatedGames: StateFlow<ImmutableList<LotofacilGame>> = gameRepository.games
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = persistentListOf()
        )
    fun onClearGamesRequested() {
        viewModelScope.launch { _uiState.update { it.copy(showClearGamesDialog = true) } }
    }
    fun confirmClearUnpinned() {
        viewModelScope.launch {
            gameRepository.clearUnpinnedGames()
            _uiState.update { it.copy(showClearGamesDialog = false) }
            _uiEvent.send(UiEvent.ShowSnackbar(message = "Jogos não fixados limpos com sucesso."))
        }
    }
    fun dismissClearDialog() {
        _uiState.update { it.copy(showClearGamesDialog = false) }
    }
    fun onDeleteGameRequested(game: LotofacilGame) {
        viewModelScope.launch { _uiState.update { it.copy(gameToDelete = game) } }
    }
    fun confirmDeleteGame(game: LotofacilGame) {
        viewModelScope.launch {
            gameRepository.deleteGame(game)
            _uiState.update { it.copy(gameToDelete = null) }
            _uiEvent.send(UiEvent.ShowSnackbar(message = "Jogo excluído com sucesso."))
        }
    }
    fun dismissDeleteDialog() {
        _uiState.update { it.copy(gameToDelete = null) }
    }
    fun analyzeGame(game: LotofacilGame) {
        if (_uiState.value.analysisState is GameAnalysisUiState.Loading) return
        viewModelScope.launch {
            _uiState.update { it.copy(analysisState = GameAnalysisUiState.Loading) }
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
                        _uiState.update { it.copy(analysisState = GameAnalysisUiState.Success(), analysisResult = result) }
                    }
                    is GameCheckState.Failure -> {
                        val messageResId = when (checkState.error) {
                            is com.cebolao.lotofacil.core.error.EmptyHistoryError -> R.string.error_no_history
                            else -> R.string.error_analysis_failed
                        }
                        _uiState.update { it.copy(analysisState = GameAnalysisUiState.Error(messageResId)) }
                    }
                    else -> Unit
                }
            } catch (_: Exception) {
                val messageResId = R.string.error_analysis_failed
                _uiState.update { it.copy(analysisState = GameAnalysisUiState.Error(messageResId)) }
                _uiEvent.send(UiEvent.ShowSnackbar(message = "Erro ao analisar jogo."))
            }
        }
    }
    fun dismissAnalysisDialog() {
        _uiState.update { it.copy(analysisResult = null, analysisState = GameAnalysisUiState.Idle) }
    }
    fun clearUnpinned() {
        viewModelScope.launch {
            gameRepository.clearUnpinnedGames()
        }
    }
    fun togglePinState(game: LotofacilGame) {
        viewModelScope.launch {
            gameRepository.togglePinState(game)
        }
    }
    fun requestDeleteGame(game: LotofacilGame) {
        viewModelScope.launch {
            _uiState.update { it.copy(gameToDelete = game) }
        }
    }
}
