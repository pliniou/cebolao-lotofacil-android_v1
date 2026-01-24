package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.components.ClickableCard
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSpacing

import com.cebolao.lotofacil.ui.theme.iconMedium
import com.cebolao.lotofacil.ui.theme.iconSmall
import com.cebolao.lotofacil.viewmodels.StatisticPattern
import kotlinx.collections.immutable.toImmutableList

@Composable
fun StatisticsSection(
    stats: StatisticsReport?,
    isStatsLoading: Boolean,
    selectedTimeWindow: Int,
    selectedPattern: StatisticPattern,
    onTimeWindowSelected: (Int) -> Unit,
    onPatternSelected: (StatisticPattern) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
    ) {
        Text(
            text = stringResource(id = R.string.statistics_center),
            style = MaterialTheme.typography.headlineSmall,
            color = colors.onSurface,
            fontWeight = FontWeight.Bold
        )
        stats?.let {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = AppCardDefaults.elevation
            ) {
                Column(
                    modifier = Modifier.padding(vertical = AppCardDefaults.defaultPadding),
                    verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
                ) {
                    StatRow(
                        title = stringResource(id = R.string.most_delayed_numbers),
                        numbers = it.mostOverdueNumbers,
                        icon = Icons.Default.HourglassEmpty,
                        suffix = stringResource(id = R.string.delayed_suffix)
                    )
                    HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
                    
                    TimeWindowSelector(selectedTimeWindow, onTimeWindowSelected)
                    
                    AnimatedContent(targetState = isStatsLoading, label = "stats_loading") { loading ->
                        if (loading) {
                            StatsLoadingSkeleton()
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)) {
                                StatRow(
                                    title = stringResource(id = R.string.most_drawn_numbers),
                                    numbers = it.mostFrequentNumbers,
                                    icon = Icons.Default.LocalFireDepartment,
                                    suffix = stringResource(id = R.string.frequency_suffix)
                                )
                                HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
                                DistributionCharts(it, selectedPattern, onPatternSelected)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeWindowSelector(selected: Int, onSelect: (Int) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val windows = listOf(0, 500, 250, 100, 50, 10)
    
    Column(
        modifier = Modifier.padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Text(
            text = stringResource(id = R.string.analysis_period_title),
            style = MaterialTheme.typography.titleMedium,
            color = colors.onSurface,
            fontWeight = FontWeight.Medium
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            items(windows, key = { it }) { window ->
                TimeWindowChip(
                    isSelected = window == selected,
                    onClick = { onSelect(window) },
                    label = if (window == 0) {
                        stringResource(id = R.string.time_window_all)
                    } else {
                        stringResource(id = R.string.time_window_last, window)
                    }
                )
            }
        }
    }
}

@Composable
private fun TimeWindowChip(isSelected: Boolean, onClick: () -> Unit, label: String) {
    val colors = MaterialTheme.colorScheme
    
    val container by animateColorAsState(
        targetValue = if (isSelected) colors.secondaryContainer else colors.surface,
        animationSpec = tween(250),
        label = "chipContainer"
    )
    val content by animateColorAsState(
        targetValue = if (isSelected) colors.primary else colors.onSurface,
        animationSpec = tween(250),
        label = "chipContent"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary.copy(alpha = 0.3f) else colors.outline.copy(alpha = 0.15f),
        animationSpec = tween(250),
        label = "chipBorder"
    )

    ClickableCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = container,
        border = BorderStroke(1.dp, borderColor),
        elevation = AppElevation.none
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = content,
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
        )
    }
}

@Composable
private fun StatRow(title: String, numbers: List<Pair<Int, Int>>, icon: ImageVector, suffix: String) {
    val colors = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier.padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(iconMedium())
                    .background(
                        colors.secondaryContainer,
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    null, 
                    tint = colors.primary,
                    modifier = Modifier.size(iconSmall())
                )
            }
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
        }
        Row(
            Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md, Alignment.CenterHorizontally)
        ) {
            for ((num, value) in numbers) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    NumberBall(num, size = 42.dp)
                    Text(
                        text = "$value$suffix", 
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DistributionCharts(
    stats: StatisticsReport,
    selected: StatisticPattern,
    onSelect: (StatisticPattern) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val patterns = remember { StatisticPattern.entries.toTypedArray() }
    
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            items(patterns, key = { it.name }) { pattern ->
                val selectedPattern = selected == pattern
                FilterChip(
                    selected = selectedPattern,
                    onClick = { onSelect(pattern) },
                    label = { 
                        Text(
                            text = stringResource(id = pattern.titleRes),
                            color = if (selectedPattern) colors.onPrimary else colors.onSurface
                        )
                    },
                    leadingIcon = { 
                        Icon(
                            pattern.icon, 
                            null, 
                            modifier = Modifier.size(iconSmall()),
                            tint = if (selectedPattern) colors.onPrimary else colors.primary
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary,
                        selectedLabelColor = colors.onPrimary,
                        selectedLeadingIconColor = colors.onPrimary
                    )
                )
            }
        }
        val data = remember(selected, stats) {
            when (selected) {
                StatisticPattern.SUM -> stats.sumDistribution
                StatisticPattern.EVENS -> stats.evenDistribution
                StatisticPattern.PRIMES -> stats.primeDistribution
                StatisticPattern.FRAME -> stats.frameDistribution
                StatisticPattern.PORTRAIT -> stats.portraitDistribution
                StatisticPattern.FIBONACCI -> stats.fibonacciDistribution
                StatisticPattern.MULTIPLES_OF_3 -> stats.multiplesOf3Distribution
            }.toList().sortedBy { it.first }.map { it.first.toString() to it.second }.toImmutableList()
        }
        // Optimize max calculation with derivedStateOf to avoid recalculating on every recomposition
        val max by remember(data) { 
            derivedStateOf { (data.maxOfOrNull { it.second } ?: 1) }
        }
        AnimatedContent(targetState = data, label = "chart") { list ->
            if (list.isNotEmpty()) {
                BarChart(
                    data = list,
                    maxValue = max,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md)
                )
            }
        }
    }
}

@Composable
fun HomeScreenSkeleton() {
    val colors = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xxl)
    ) {
        // Last Draw Skeleton
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(AppCardDefaults.defaultPadding),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Box(Modifier.size(150.dp, 20.dp).clip(MaterialTheme.shapes.small).shimmer())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0 until 5) {
                        Box(Modifier.size(40.dp).clip(MaterialTheme.shapes.medium).shimmer())
                    }
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.outline.copy(alpha = 0.1f)))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md, Alignment.CenterHorizontally)
                ) {
                    for (i in 0 until 3) {
                        Box(Modifier.size(60.dp, 30.dp).clip(MaterialTheme.shapes.small).shimmer())
                    }
                }
            }
        }

        // Statistics Skeleton
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            Box(Modifier.size(200.dp, 28.dp).clip(MaterialTheme.shapes.small).shimmer())
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(vertical = AppCardDefaults.defaultPadding),
                    verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
                ) {
                    for (i in 0 until 2) {
                        Column(Modifier.padding(horizontal = AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                            Box(Modifier.size(150.dp, 20.dp).clip(MaterialTheme.shapes.small).shimmer())
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                for (j in 0 until 5) {
                                    Box(Modifier.size(40.dp).clip(MaterialTheme.shapes.medium).shimmer())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsLoadingSkeleton() {
    val colors = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier.padding(vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        Column(Modifier.padding(horizontal = AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Box(Modifier.size(150.dp, 20.dp).clip(MaterialTheme.shapes.small).shimmer())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (i in 0 until 5) {
                    Box(Modifier.size(40.dp).clip(MaterialTheme.shapes.medium).shimmer())
                }
            }
        }
        HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
        Column(Modifier.padding(horizontal = AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                for (i in 0 until 4) {
                    Box(Modifier.size(80.dp, 32.dp).clip(MaterialTheme.shapes.medium).shimmer())
                }
            }
            Box(Modifier.fillMaxWidth().height(150.dp).clip(MaterialTheme.shapes.medium).shimmer())
        }
    }
}
