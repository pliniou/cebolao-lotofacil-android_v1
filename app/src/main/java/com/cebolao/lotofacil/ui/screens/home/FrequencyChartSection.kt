package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.InsightsViewModel
import kotlinx.collections.immutable.toImmutableList
import androidx.compose.runtime.remember

@Composable
fun FrequencyChartSection(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val analysis = uiState.frequencyAnalysis ?: return

    val chartData = remember(analysis.frequencies) {
        analysis.frequencies.entries
            .sortedBy { it.key }
            .map { it.key.toString() to it.value }
            .toImmutableList()
    }
    
    val maxValue = remember(analysis.frequencies) {
        analysis.frequencies.values.maxOrNull() ?: 1
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = stringResource(id = R.string.frequency_summary_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            BarChart(
                data = chartData,
                maxValue = maxValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.md),
                chartHeight = 160.dp,
                showGaussCurve = false
            )
        }
    }
}
