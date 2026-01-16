package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.EmptyState
import com.cebolao.lotofacil.ui.components.GameStatsList
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.LoadingDialog
import com.cebolao.lotofacil.ui.components.RecentHitsChartContent
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.components.cards.GameCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.GameAnalysisResult
import com.cebolao.lotofacil.viewmodels.GameAnalysisUiState
import com.cebolao.lotofacil.viewmodels.GameUiEvent
import com.cebolao.lotofacil.viewmodels.GameViewModel

@Composable
fun GeneratedGamesScreen(
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val games by gameViewModel.generatedGames.collectAsStateWithLifecycle()
    val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        gameViewModel.uiEvent.collect { event ->
            when (event) {
                is com.cebolao.lotofacil.navigation.UiEvent.ShowSnackbar -> {
                    val message = event.message ?: event.messageResId?.let(context::getString).orEmpty()
                    if (message.isNotBlank()) {
                        snackbarHostState.showSnackbar(message = message)
                    }
                }
                else -> {}
            }
        }
    }
    
    // Optimize expensive calculations with derivedStateOf
    val hasUnpinnedGames by remember {
        derivedStateOf { games.any { !it.isPinned } }
    }
    val isGamesEmpty by remember {
        derivedStateOf { games.isEmpty() }
    }

    if (uiState.showClearGamesDialog) {
        AlertDialog(
            onDismissRequest = { gameViewModel.dismissClearDialog() },
            title = { Text(stringResource(id = R.string.clear_games_title)) },
            text = { Text(stringResource(id = R.string.clear_games_message)) },
            confirmButton = {
                Button(onClick = { gameViewModel.confirmClearUnpinned() }) { 
                    Text(stringResource(id = R.string.clear_button)) 
                }
            },
            dismissButton = {
                TextButton(onClick = { gameViewModel.dismissClearDialog() }) { 
                    Text(stringResource(id = R.string.cancel_button)) 
                }
            }
        )
    }

    uiState.gameToDelete?.let { game ->
        AlertDialog(
            onDismissRequest = { gameViewModel.dismissDeleteDialog() },
            title = { Text(stringResource(id = R.string.delete_game_title)) },
            text = { Text(stringResource(id = R.string.delete_game_message)) },
            confirmButton = {
                Button(onClick = { gameViewModel.confirmDeleteGame(game) }) { 
                    Text(stringResource(id = R.string.delete_button)) 
                }
            },
            dismissButton = {
                TextButton(onClick = { gameViewModel.dismissDeleteDialog() }) { 
                    Text(stringResource(id = R.string.cancel_button)) 
                }
            }
        )
    }

    when (val state = uiState.analysisState) {
        is GameAnalysisUiState.Success -> {
            uiState.analysisResult?.let { result ->
                GameAnalysisDialog(
                    result = result,
                    onDismissRequest = { gameViewModel.dismissAnalysisDialog() }
                )
            }
        }
        is GameAnalysisUiState.Loading -> {
            LoadingDialog(text = stringResource(id = R.string.analyzing_game))
        }
        is GameAnalysisUiState.Error -> {
            AlertDialog(
                onDismissRequest = { gameViewModel.dismissAnalysisDialog() },
                title = { Text(stringResource(id = R.string.analysis_error_title)) },
                text = { Text(stringResource(id = state.messageResId)) },
                confirmButton = {
                    TextButton(onClick = { gameViewModel.dismissAnalysisDialog() }) {
                        Text(stringResource(id = R.string.close_button))
                    }
                }
            )
        }
        is GameAnalysisUiState.Idle -> {}
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.my_games),
                subtitle = stringResource(id = R.string.my_games_subtitle),
                icon = Icons.AutoMirrored.Filled.ListAlt,
                actions = {
                    if (hasUnpinnedGames) {
                        IconButton(onClick = { gameViewModel.clearUnpinned() }) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = stringResource(id = R.string.clear_unpinned_games),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {

            if (isGamesEmpty) {
                item {
                    EmptyState(messageResId = R.string.empty_games_message)
                }
            } else {
                itemsIndexed(games, key = { _, game -> game.id }) { index, game ->
                    // Optimize animation delay calculation
                    val animationDelay = remember(index) { 
                        ((index * 60).coerceAtMost(500)).toLong() 
                    }
                    AnimateOnEntry(delayMillis = animationDelay) {
                        Box(Modifier.padding(horizontal = AppSpacing.lg)) {
                            GameCard(
                                game = game,
                                onAnalyzeClick = { gameViewModel.analyzeGame(game) },
                                onPinClick = { gameViewModel.togglePinState(game) },
                                onDeleteClick = { gameViewModel.requestDeleteGame(game) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameAnalysisDialog(
    result: GameAnalysisResult,
    onDismissRequest: () -> Unit
) {
    InfoDialog(
        onDismissRequest = onDismissRequest,
        dialogTitle = stringResource(id = R.string.game_analysis_title),
        dismissButtonText = stringResource(id = R.string.close_button)
    ) {
        Text(
            stringResource(id = R.string.historical_performance),
            style = MaterialTheme.typography.titleMedium,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.xs))
        if (result.checkResult.scoreCounts.isEmpty()) {
            Text(
                stringResource(id = R.string.never_won_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            CheckResultCard(result = result.checkResult)
        }
        RecentHitsChartContent(
            recentHits = result.checkResult.recentHits,
            modifier = Modifier.padding(top = AppSpacing.lg)
        )
        Text(
            stringResource(id = R.string.game_stats_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = AppSpacing.lg)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.xs))
        GameStatsList(stats = result.simpleStats)

    }
}

