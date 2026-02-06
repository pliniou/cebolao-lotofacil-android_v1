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
            uiState.isLoading -> {
                LoadingData(
                    message = stringResource(id = R.string.loading_data),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            uiState.errorMessageResId != null -> {
                ErrorCard(
                    messageResId = uiState.errorMessageResId!!,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(AppSpacing.lg),
                    actions = {
                        viewModel.loadFrequencyAnalysis()
                    }
                )
            }
            uiState.frequencyAnalysis != null -> {
                val analysis = uiState.frequencyAnalysis!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(AppSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    item(key = "top_numbers") {
                        TopNumbersSection(topNumbers = analysis.topNumbers)
                    }

                    item(key = "recency") {
                        RecencySection(overdueNumbers = analysis.overdueNumbers)
                    }

                    item(key = "pattern_list") {
                        PatternListSection(
                            analysis = uiState.patternAnalysis,
                            selectedSize = uiState.selectedPatternSize,
                            onSizeSelected = { viewModel.onPatternSizeSelected(it) }
                        )
                    }

                    item(key = "trend") {
                        TrendSection(
                            analysis = uiState.trendAnalysis,
                            selectedType = uiState.selectedTrendType,
                            selectedWindow = uiState.selectedTrendWindow,
                            onTypeSelected = { viewModel.onTrendTypeSelected(it) },
                            onWindowSelected = { viewModel.onTrendWindowSelected(it) }
                        )
                    }
                    
                    item(key = "frequency_chart") {
                        FrequencyBarChart(frequencies = analysis.frequencies)
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


