package com.cebolao.lotofacil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.data.LotofacilGame
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.GameCard
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.LoadingDialog
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.viewmodels.GameAnalysisResult
import com.cebolao.lotofacil.viewmodels.GameAnalysisUiState
import com.cebolao.lotofacil.viewmodels.GameViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun GeneratedGamesScreen(
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val games by gameViewModel.generatedGames.collectAsStateWithLifecycle()
    val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val analysisState by gameViewModel.analysisState.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Limpar Jogos?") },
            text = { Text("Isso removerá todos os jogos não fixados. Deseja continuar?") },
            confirmButton = {
                Button(onClick = {
                    gameViewModel.clearUnpinned()
                    showClearDialog = false
                }) { Text("Limpar") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancelar") }
            }
        )
    }

    uiState.gameToDelete?.let {
        AlertDialog(
            onDismissRequest = { gameViewModel.dismissDeleteDialog() },
            title = { Text("Excluir Jogo?") },
            text = { Text("Esta ação é permanente. Deseja excluir este jogo?") },
            confirmButton = {
                Button(onClick = { gameViewModel.confirmDeleteGame() }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { gameViewModel.dismissDeleteDialog() }) { Text("Cancelar") }
            }
        )
    }

    when (val state = analysisState) {
        is GameAnalysisUiState.Success -> {
            GameAnalysisDialog(
                result = state.result,
                onDismissRequest = { gameViewModel.dismissAnalysisDialog() }
            )
        }
        is GameAnalysisUiState.Loading -> {
            LoadingDialog(text = "Analisando jogo...")
        }
        is GameAnalysisUiState.Error -> {
            // O tratamento de erro agora pode ser mais robusto, talvez com um Snackbar ou um Dialog
        }
        is GameAnalysisUiState.Idle -> {}
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            StandardScreenHeader(
                title = "Meus Jogos",
                subtitle = "Visualize, analise e gerencie seus jogos",
                icon = Icons.AutoMirrored.Filled.ListAlt,
                actions = {
                    if (games.any { !it.isPinned }) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Limpar jogos não fixados")
                        }
                    }
                }
            )
            AnimatedVisibility(
                visible = games.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmptyState()
            }
            AnimatedVisibility(
                visible = games.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                GamesList(
                    games = games,
                    onAnalyzeClick = { gameViewModel.analyzeGame(it) },
                    onPinClick = { gameViewModel.togglePinState(it) },
                    onDeleteClick = { gameViewModel.requestDeleteGame(it) }
                )
            }
        }
    }
}

@Composable
private fun GamesList(
    games: ImmutableList<LotofacilGame>,
    onAnalyzeClick: (LotofacilGame) -> Unit,
    onPinClick: (LotofacilGame) -> Unit,
    onDeleteClick: (LotofacilGame) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = games,
            key = { game -> game.numbers.sorted().joinToString("-") }
        ) { game ->
            AnimateOnEntry {
                GameCard(
                    game = game,
                    onAnalyzeClick = { onAnalyzeClick(game) },
                    onPinClick = { onPinClick(game) },
                    onDeleteClick = { onDeleteClick(game) }
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Nenhum jogo gerado ainda.\nVá para a tela de Gerador para começar!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GameAnalysisDialog(
    result: GameAnalysisResult,
    onDismissRequest: () -> Unit
) {
    InfoDialog(
        onDismissRequest = onDismissRequest,
        dialogTitle = "Análise do Jogo",
        dismissButtonText = "Fechar"
    ) {
        Text(
            "Desempenho Histórico",
            style = MaterialTheme.typography.titleMedium,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        if (result.checkResult.scoreCounts.isEmpty()) {
            Text(
                "Este jogo nunca foi premiado nos concursos analisados.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            CheckResultCard(result = result.checkResult)
        }
        Text(
            "Acertos nos Últimos 15 Sorteios",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        val chartData = result.checkResult.recentHits.map { it.first.toString().takeLast(4) to it.second }
        val maxValue = (chartData.maxOfOrNull { it.second }?.coerceAtLeast(10) ?: 10)
        BarChart(
            data = chartData.toImmutableList(),
            maxValue = maxValue,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        Text(
            "Estatísticas do Jogo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        result.simpleStats.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.bodyMedium)
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}