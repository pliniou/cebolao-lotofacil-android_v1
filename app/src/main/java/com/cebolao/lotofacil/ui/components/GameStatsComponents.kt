package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun GameStatsList(
    stats: ImmutableList<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        stats.forEach { (label, value) ->
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

@Composable
fun RecentHitsChartContent(
    recentHits: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 180.dp
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Text(stringResource(id = R.string.recent_hits_title), style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.xs))
        val chartData = remember(recentHits) {
            recentHits.map { it.first.toString().takeLast(4) to it.second }.toImmutableList()
        }
        val maxValue = remember(chartData) {
            (chartData.maxOfOrNull { it.second }?.coerceAtLeast(10) ?: 10)
        }
        BarChart(
            data = chartData,
            maxValue = maxValue,
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        )
    }
}
