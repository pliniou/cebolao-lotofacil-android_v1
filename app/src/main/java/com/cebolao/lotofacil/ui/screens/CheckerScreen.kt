package com.cebolao.lotofacil.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.data.LotofacilConstants
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.BottomActionsBar
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.GameStatsList
import com.cebolao.lotofacil.ui.components.NumberBallItem
import com.cebolao.lotofacil.ui.components.NumberGrid
import com.cebolao.lotofacil.ui.components.RecentHitsChartContent
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.CheckerUiState
import com.cebolao.lotofacil.viewmodels.CheckerViewModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CheckerScreen(checkerViewModel: CheckerViewModel =  hiltViewModel()) {
    val checkerState by checkerViewModel.uiState.collectAsStateWithLifecycle()
    val selectedNumbers by checkerViewModel.selectedNumbers.collectAsStateWithLifecycle()
    val isButtonEnabled by remember(selectedNumbers, checkerState) {
        derivedStateOf {
            selectedNumbers.size == LotofacilConstants.GAME_SIZE && checkerState !is CheckerUiState.Loading
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomActionsBar(
                selectedCount = selectedNumbers.size,
                isLoading = checkerState is CheckerUiState.Loading,
                isButtonEnabled = isButtonEnabled,
                onClearClick = { checkerViewModel.clearSelection() },
                onCheckClick = { checkerViewModel.onCheckGameClicked() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)
        ) {
            item {
                StandardScreenHeader(
                    title = stringResource(id = R.string.check_game_title),
                    subtitle = stringResource(id = R.string.check_game_subtitle),
                    icon = Icons.AutoMirrored.Filled.FactCheck
                )
            }
            item {
                AnimateOnEntry(delayMillis = 100L) {
                    Box(Modifier.padding(horizontal = AppSpacing.lg)) {
                        NumberSelectionCard(
                            selectedNumbers = selectedNumbers,
                            onNumberClick = { checkerViewModel.onNumberClicked(it) }
                        )
                    }
                }
            }
            item {
                Box(Modifier.padding(horizontal = AppSpacing.lg)) {
                    AnimatedContent(
                        targetState = checkerState,
                        label = "result-content"
                    ) { state ->
                        when (state) {
                            is CheckerUiState.Success -> ResultSection(state)
                            is CheckerUiState.Error -> ErrorCard(state.messageResId)
                            is CheckerUiState.Loading -> CheckerResultSkeleton()
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckerResultSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        repeat(3) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(AppCardDefaults.defaultPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(Modifier.size(150.dp, 20.dp).clip(MaterialTheme.shapes.small).shimmer())
                    Box(Modifier.fillMaxWidth().height(80.dp).clip(MaterialTheme.shapes.medium).shimmer())
                }
            }
        }
    }
}


@Composable
private fun NumberSelectionCard(
    selectedNumbers: Set<Int>,
    onNumberClick: (Int) -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = AppCardDefaults.elevation
    ) {
        Box(Modifier.padding(AppCardDefaults.defaultPadding)) {
            val numberItems = remember(selectedNumbers) {
                val selectionSize = selectedNumbers.size
                (1..25).map { number ->
                    val isSelected = number in selectedNumbers
                    NumberBallItem(
                        number = number,
                        isSelected = isSelected,
                        isDisabled = selectionSize >= LotofacilConstants.GAME_SIZE && !isSelected
                    )
                }
            }
            
            NumberGrid(
                items = numberItems,
                onNumberClick = onNumberClick
            )
        }
    }
}

@Composable
private fun ResultSection(state: CheckerUiState.Success) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CheckResultCard(state.result)
        SimpleStatsCard(state.simpleStats)
        BarChartCard(state.result)
    }
}

@Composable
private fun SimpleStatsCard(stats: ImmutableList<Pair<String, String>>) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = AppCardDefaults.elevation
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            Text(stringResource(id = R.string.game_stats_title), style = MaterialTheme.typography.titleMedium)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            GameStatsList(stats = stats)
        }
    }
}

@Composable
private fun BarChartCard(result: com.cebolao.lotofacil.data.CheckResult) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = AppCardDefaults.elevation
    ) {
        Column(
            Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            RecentHitsChartContent(recentHits = result.recentHits)
        }
    }
}

@Composable
private fun SelectionProgress(count: Int) {
    val progress = count / 15f
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(MaterialTheme.shapes.small),
            strokeCap = StrokeCap.Round
        )
        Text(
            stringResource(id = R.string.selected_numbers_progress, count),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
