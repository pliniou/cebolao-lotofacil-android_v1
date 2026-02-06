package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppAlpha
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlinx.collections.immutable.toImmutableList

@Stable
data class ChartData(
    val entries: kotlinx.collections.immutable.ImmutableList<Pair<String, Int>>,
    val maxValue: Int
)

@Composable
fun FrequencyBarChart(
    frequencies: Map<Int, Int>,
    modifier: Modifier = Modifier,
    showGaussCurve: Boolean = false,
    onGaussCurveToggle: (Boolean) -> Unit = {}
) {
    val chartData = remember(frequencies) {
        val entries = frequencies.entries
            .sortedBy { it.key }
            .map { it.key.toString() to it.value }
            .toImmutableList()
        val maxValue = frequencies.values.maxOrNull() ?: 0
        ChartData(entries, maxValue)
    }

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                }
                
                GaussianCurveToggle(
                    showGaussCurve = showGaussCurve,
                    onToggle = onGaussCurveToggle
                )
            }
            
            BarChart(
                data = chartData.entries,
                maxValue = chartData.maxValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.lg),
                chartHeight = 240.dp,
                showGaussCurve = showGaussCurve
            )
        }
    }
}

@Composable
private fun GaussianCurveToggle(
    showGaussCurve: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val toggleInteractionSource = remember { MutableInteractionSource() }
    val hapticFeedback = LocalHapticFeedback.current
    val toggleStateLabel = if (showGaussCurve) {
        stringResource(R.string.gaussian_toggle_active)
    } else {
        stringResource(R.string.gaussian_toggle_inactive)
    }
    val toggleDescription = stringResource(
        R.string.gaussian_toggle_description,
        toggleStateLabel
    )
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (showGaussCurve) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(toggleInteractionSource, indication = null) { 
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle(!showGaussCurve) 
            }
            .focusable(interactionSource = toggleInteractionSource)
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter || keyEvent.key == Key.Spacebar) {
                    if (keyEvent.type == KeyEventType.KeyUp) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggle(!showGaussCurve)
                        true
                    } else false
                } else false
            }
            .padding(AppSpacing.xs)
            .semantics {
                contentDescription = toggleDescription
                role = Role.Switch
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs)
        ) {
            Icon(
                imageVector = if (showGaussCurve) Icons.Filled.Timeline else Icons.Filled.BarChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = if (showGaussCurve) {
                    stringResource(id = R.string.gaussian_toggle_label_on)
                } else {
                    stringResource(id = R.string.gaussian_toggle_label_off)
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TopNumbersSection(
    topNumbers: List<Int>,
    modifier: Modifier = Modifier
) {
    val rankedNumbers = remember(topNumbers) { topNumbers.toImmutableList() }

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
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                itemsIndexed(rankedNumbers, key = { _, number -> number }) { index, number ->
                    RankedNumberBall(
                        position = index + 1,
                        number = number
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingBadge(position: Int, modifier: Modifier = Modifier) {
    val badgeConfig = when (position) {
        1 -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        2 -> Pair(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
        3 -> Pair(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
        else -> Pair(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.surface)
    }
    val (badgeColor, textColor) = badgeConfig

    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(badgeColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = position.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = AppAlpha.textSecondary)
        )
    }
}

@Composable
private fun RankedNumberBall(position: Int, number: Int, modifier: Modifier = Modifier) {
    val rankedNumberDescription = stringResource(
        R.string.ranked_number_description,
        position,
        number
    )
    Box(modifier = modifier
        .semantics {
            contentDescription = rankedNumberDescription
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.TopStart) {
                NumberBall(
                    number = number,
                    isHighlighted = position <= 3,
                    size = AppSize.numberBallMedium
                )
                RankingBadge(position = position)
            }
            Text(
                text = stringResource(id = R.string.position_abbr, position),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = AppSpacing.xs)
            )
        }
    }
}
