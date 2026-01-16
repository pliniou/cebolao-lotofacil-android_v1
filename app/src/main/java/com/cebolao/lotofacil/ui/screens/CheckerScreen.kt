package com.cebolao.lotofacil.ui.screens

import androidx.compose.animation.AnimatedContent
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
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.BottomActionsBar
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.ErrorActions
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
fun CheckerScreen(checkerViewModel: CheckerViewModel = hiltViewModel()) {
    val screenState by checkerViewModel.uiState.collectAsStateWithLifecycle()
    val isButtonEnabled by remember(screenState) {
        derivedStateOf {
            screenState.selectedNumbers.size == LotofacilConstants.GAME_SIZE && screenState.uiState !is CheckerUiState.Loading
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.check_game_title),
                subtitle = stringResource(id = R.string.check_game_subtitle),
                icon = Icons.AutoMirrored.Filled.FactCheck
            )
        },
        bottomBar = {
            BottomActionsBar(
                selectedCount = screenState.selectedNumbers.size,
                isLoading = screenState.uiState is CheckerUiState.Loading,
                isButtonEnabled = isButtonEnabled,
                onClearClick = checkerViewModel::clearSelection,
                onCheckClick = checkerViewModel::onCheckGameClicked
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                item {
                    NumberGridSection(
                        selectedNumbers = screenState.selectedNumbers,
                        onNumberClicked = checkerViewModel::onNumberClicked
                    )
                }

                item {
                    val currentUiState = screenState.uiState
                    AnimatedContent(
                        targetState = currentUiState,
                        label = "checker_result_content"
                    ) { state ->
                        when (state) {
                            is CheckerUiState.Idle -> { /* Nothing shown */ }
                            is CheckerUiState.Loading -> {
                                CheckerLoadingContent(state.progress, state.message)
                            }
                            is CheckerUiState.Success -> {
                                CheckerSuccessContent(
                                    result = state.result,
                                    stats = state.simpleStats
                                )
                            }
                            is CheckerUiState.Error -> {
                                ErrorCard(
                                    messageResId = state.messageResId,
                                    actions = {
                                        if (state.canRetry) {
                                            ErrorActions(
                                                onRetry = checkerViewModel::onCheckGameClicked,
                                                retryText = stringResource(id = R.string.try_again)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberGridSection(
    selectedNumbers: Set<Int>,
    onNumberClicked: (Int) -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = AppCardDefaults.elevation
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            Text(
                text = stringResource(id = R.string.selected_numbers_progress, selectedNumbers.size),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            val gridItems = remember(selectedNumbers) {
                (1..25).map { num ->
                    NumberBallItem(
                        number = num,
                        isSelected = selectedNumbers.contains(num),
                        isDisabled = false
                    )
                }
            }
            NumberGrid(
                items = gridItems,
                onNumberClick = onNumberClicked
            )
        }
    }
}

@Composable
private fun CheckerLoadingContent(progress: Float, message: String) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                strokeCap = StrokeCap.Round
            )
            CheckerResultSkeleton()
        }
    }
}

@Composable
private fun CheckerSuccessContent(
    result: com.cebolao.lotofacil.domain.model.CheckResult,
    stats: ImmutableList<Pair<String, String>>
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        AnimateOnEntry {
            CheckResultCard(result = result)
        }

        AnimateOnEntry(delayMillis = 100) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(AppCardDefaults.defaultPadding),
                    verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
                ) {
                    Text(
                        text = stringResource(id = R.string.game_stats_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    GameStatsList(stats = stats)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    RecentHitsChartContent(recentHits = result.recentHits)
                }
            }
        }
    }
}

@Composable
private fun CheckerResultSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .shimmer()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            for (i in 0 until 3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmer()
                )
            }
        }
    }
}
