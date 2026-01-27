package com.cebolao.lotofacil.ui.screens.generated

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.constants.AppConstants
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.EmptyState
import com.cebolao.lotofacil.ui.components.GameStatsList
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.LoadingDialog
import com.cebolao.lotofacil.ui.components.RecentHitsChartContent
import com.cebolao.lotofacil.ui.components.SnackbarHost
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.components.cards.GameCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.GameAnalysisResult
import com.cebolao.lotofacil.viewmodels.GameAnalysisUiState
import com.cebolao.lotofacil.viewmodels.GameViewModel

@Composable
fun GeneratedGamesScreen(
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val games by gameViewModel.generatedGames.collectAsStateWithLifecycle()
    val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        gameViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
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
        ConfirmationDialog(
            title = stringResource(id = R.string.clear_games_title),
            message = stringResource(id = R.string.clear_games_message),
            confirmText = stringResource(id = R.string.clear_button),
            dismissText = stringResource(id = R.string.cancel_button),
            onConfirm = { gameViewModel.confirmClearUnpinned() },
            onDismiss = { gameViewModel.dismissClearDialog() }
        )
    }

    uiState.gameToDelete?.let { game ->
        ConfirmationDialog(
            title = stringResource(id = R.string.delete_game_title),
            message = stringResource(id = R.string.delete_game_message),
            confirmText = stringResource(id = R.string.delete_button),
            dismissText = stringResource(id = R.string.cancel_button),
            onConfirm = { gameViewModel.confirmDeleteGame(game) },
            onDismiss = { gameViewModel.dismissDeleteDialog() }
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
            ConfirmationDialog(
                title = stringResource(id = R.string.analysis_error_title),
                message = stringResource(id = state.messageResId),
                confirmText = stringResource(id = R.string.close_button),
                dismissText = "",
                onConfirm = { gameViewModel.dismissAnalysisDialog() },
                onDismiss = { gameViewModel.dismissAnalysisDialog() }
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
                                contentDescription = stringResource(id = R.string.cd_clear_games),
                                tint = colors.error
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
                top = AppSpacing.lg,
                start = AppSpacing.lg,
                end = AppSpacing.lg,
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
                        ((index * AppConstants.STAGGER_DELAY_MS).coerceAtMost(AppConstants.MAX_STAGGER_DELAY_MS)).toLong() 
                    }
                    AnimateOnEntry(delayMillis = animationDelay) {
                        // GameCard internally uses standard components, but we wrap it in ElevatedCard here if GameCard isn't elevated yet,
                        // or we rely on GameCard being updated. Let's assume GameCard is a shared component we might need to update.
                        // For now, focusing on the Screen layout which looks correct.
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
