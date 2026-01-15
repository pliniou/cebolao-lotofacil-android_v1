package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.data.CheckResult
import com.cebolao.lotofacil.data.LotofacilGame
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
    val isLoading: Boolean = false
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
) : ViewModel() {

    private val _events = Channel<GameUiEvent>()
    val events: Flow<GameUiEvent> = _events.receiveAsFlow()

    val generatedGames: StateFlow<ImmutableList<LotofacilGame>> = gameRepository.games
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = persistentListOf()
        )

    private val _uiState = MutableStateFlow(GameScreenUiState())
    val uiState: StateFlow<GameScreenUiState> = _uiState.asStateFlow()

    private val _analysisState = MutableStateFlow<GameAnalysisUiState>(GameAnalysisUiState.Idle)
    val analysisState: StateFlow<GameAnalysisUiState> = _analysisState.asStateFlow()

    fun clearUnpinned() = viewModelScope.launch {
        _events.send(GameUiEvent.ShowClearGamesDialog)
    }

    fun confirmClearUnpinned() = viewModelScope.launch {
        gameRepository.clearUnpinnedGames()
        _events.send(GameUiEvent.HideClearGamesDialog)
    }

    fun dismissClearDialog() = viewModelScope.launch {
        _events.send(GameUiEvent.HideClearGamesDialog)
    }

    fun togglePinState(gameToToggle: LotofacilGame) = viewModelScope.launch {
        gameRepository.togglePinState(gameToToggle)
    }

    fun requestDeleteGame(game: LotofacilGame) = viewModelScope.launch {
        _events.send(GameUiEvent.ShowDeleteGameDialog(game))
    }

    fun confirmDeleteGame(game: LotofacilGame) = viewModelScope.launch {
        gameRepository.deleteGame(game)
        _events.send(GameUiEvent.HideDeleteGameDialog)
    }

    fun dismissDeleteDialog() = viewModelScope.launch {
        _events.send(GameUiEvent.HideDeleteGameDialog)
    }

    fun analyzeGame(game: LotofacilGame) {
        // Prevent duplicate analysis requests
        if (_analysisState.value is GameAnalysisUiState.Loading) return
        
        viewModelScope.launch {
            _analysisState.value = GameAnalysisUiState.Loading
            try {
                val checkUiState = checkGameUseCase(game.numbers)
                    .first { it is CheckerUiState.Success || it is CheckerUiState.Error }

                if (checkUiState is CheckerUiState.Success) {
                    val result = GameAnalysisResult(
                        game = game,
                        simpleStats = checkUiState.simpleStats,
                        checkResult = checkUiState.result
                    )
                    _analysisState.value = GameAnalysisUiState.Success(result)
                    _events.send(GameUiEvent.ShowAnalysisDialog(result))
                } else {
                    val errorResId = (checkUiState as? CheckerUiState.Error)?.messageResId ?: R.string.error_analysis_failed
                    _analysisState.value = GameAnalysisUiState.Error(errorResId)
                }
            } catch (_: Exception) {
                _analysisState.value = GameAnalysisUiState.Error(R.string.error_analysis_failed)
            }
        }
    }

    fun dismissAnalysisDialog() = viewModelScope.launch {
        _events.send(GameUiEvent.HideAnalysisDialog)
        if (_analysisState.value !is GameAnalysisUiState.Idle) {
            _analysisState.value = GameAnalysisUiState.Idle
        }
    }
}