package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlinx.collections.immutable.toImmutableList

@Composable
fun FrequencyBarChart(
    frequencies: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    val chartData = remember(frequencies) {
        frequencies.entries
            .sortedBy { it.key }
            .map { it.key.toString() to it.value }
            .toImmutableList()
    }
    
    val maxValue = remember(frequencies) {
        frequencies.values.maxOrNull() ?: 0
    }

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = stringResource(id = R.string.frequency_analysis_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.frequency_chart_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            BarChart(
                data = chartData,
                maxValue = maxValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.lg),
                chartHeight = 240.dp,
                showGaussCurve = false
            )
        }
    }
}

@Composable
fun TopNumbersSection(
    topNumbers: List<Int>,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = stringResource(id = R.string.top_numbers_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(id = R.string.top_numbers_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.md),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                topNumbers.forEach { number ->
                    NumberBall(
                        number = number,
                        isHighlighted = true,
                        size = 48.dp
                    )
                }
            }
        }
    }
}
