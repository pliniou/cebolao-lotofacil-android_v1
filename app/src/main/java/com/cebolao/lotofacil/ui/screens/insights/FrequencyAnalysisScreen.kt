package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.EmptyState
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.ExpandableSection
import com.cebolao.lotofacil.ui.components.LoadingData
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.InsightsViewModel

@Composable
fun FrequencyAnalysisScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Delegate to Content composable
    FrequencyAnalysisScreenContent(
        state = uiState,
        modifier = modifier,
        onNavigateBack = onNavigateBack,
        onAction = { action ->
            when (action) {
                is FrequencyAnalysisAction.LoadFrequencyAnalysis -> viewModel.loadFrequencyAnalysis()
                is FrequencyAnalysisAction.PatternSizeSelected -> viewModel.onPatternSizeSelected(action.size)
                is FrequencyAnalysisAction.TrendTypeSelected -> viewModel.onTrendTypeSelected(action.type)
                is FrequencyAnalysisAction.TrendWindowSelected -> viewModel.onTrendWindowSelected(action.window)
            }
        }
    )
}

// ==================== SEALED ACTIONS ====================
sealed class FrequencyAnalysisAction {
    object LoadFrequencyAnalysis : FrequencyAnalysisAction()
    data class PatternSizeSelected(val size: Int) : FrequencyAnalysisAction()
    data class TrendTypeSelected(val type: String) : FrequencyAnalysisAction()
    data class TrendWindowSelected(val window: String) : FrequencyAnalysisAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun FrequencyAnalysisScreenContent(
    state: InsightsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAction: (FrequencyAnalysisAction) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.insights_title),
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingData(
                    message = stringResource(id = R.string.loading_data),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            state.errorMessageResId != null -> {
                ErrorCard(
                    messageResId = state.errorMessageResId!!,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(AppSpacing.lg),
                    actions = {
                        onAction(FrequencyAnalysisAction.LoadFrequencyAnalysis)
                    }
                )
            }
            state.frequencyAnalysis != null -> {
                val analysis = state.frequencyAnalysis!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(AppSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    item(key = "frequency_chart") {
                        ExpandableSection(
                            title = stringResource(id = R.string.frequency_analysis_title),
                            isExpandedByDefault = true
                        ) {
                            FrequencyBarChart(frequencies = analysis.frequencies)
                        }
                    }

                    item(key = "top_numbers") {
                        ExpandableSection(
                            title = stringResource(id = R.string.top_numbers_title),
                            isExpandedByDefault = false
                        ) {
                            TopNumbersSection(topNumbers = analysis.topNumbers)
                        }
                    }

                    item(key = "recency") {
                        ExpandableSection(
                            title = stringResource(id = R.string.recency_title),
                            isExpandedByDefault = false
                        ) {
                            RecencySection(overdueNumbers = analysis.overdueNumbers)
                        }
                    }

                    item(key = "pattern_list") {
                        ExpandableSection(
                            title = stringResource(id = R.string.patterns_title),
                            isExpandedByDefault = false
                        ) {
                            PatternListSection(
                                analysis = state.patternAnalysis,
                                selectedSize = state.selectedPatternSize,
                                onSizeSelected = { size -> onAction(FrequencyAnalysisAction.PatternSizeSelected(size)) }
                            )
                        }
                    }

                    item(key = "trend") {
                        ExpandableSection(
                            title = stringResource(id = R.string.trend_analysis_title),
                            isExpandedByDefault = false
                        ) {
                            TrendSection(
                                analysis = state.trendAnalysis,
                                selectedType = state.selectedTrendType,
                                selectedWindow = state.selectedTrendWindow,
                                onTypeSelected = { type -> onAction(FrequencyAnalysisAction.TrendTypeSelected(type)) },
                                onWindowSelected = { window -> onAction(FrequencyAnalysisAction.TrendWindowSelected(window)) }
                            )
                        }
                    }
                    
                    item(key = "total_draws") {
                        Text(
                            text = stringResource(id = R.string.total_draws_analyzed_format, analysis.totalDraws),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = AppSpacing.sm)
                        )
                    }
                }
            }
            else -> {
                EmptyState(
                    messageResId = R.string.error_no_history,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


