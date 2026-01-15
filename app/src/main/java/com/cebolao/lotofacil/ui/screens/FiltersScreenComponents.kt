package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.data.FilterState
import com.cebolao.lotofacil.data.FilterType
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.FilterCard
import com.cebolao.lotofacil.ui.components.FilterStatsPanel
import com.cebolao.lotofacil.ui.components.GenerationActionsPanel
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.GenerationUiState

@Composable
fun FiltersHeader(
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    
    StandardScreenHeader(
        modifier = modifier,
        title = stringResource(id = R.string.filters_header_title),
        subtitle = stringResource(id = R.string.filters_header_subtitle),
        icon = Icons.Default.FilterAlt,
        actions = {
            IconButton(onClick = {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onResetFilters()
            }) {
                Icon(Icons.Default.FilterAlt, androidx.compose.ui.res.stringResource(id = R.string.reset_filters))
            }
        }
    )
}

@Composable
fun ActiveFiltersPanel(
    activeFilters: List<FilterState>,
    successProbability: Float,
    modifier: Modifier = Modifier
) {
    AnimateOnEntry(
        modifier = modifier.padding(horizontal = AppSpacing.lg)
    ) {
        FilterStatsPanel(
            activeFilters = activeFilters,
            successProbability = successProbability
        )
    }
}

fun LazyListScope.filterList(
    filterStates: List<FilterState>,
    lastDraw: Set<Int>?,
    onFilterToggle: (FilterType, Boolean) -> Unit,
    onRangeChange: (FilterType, kotlin.ranges.ClosedFloatingPointRange<Float>) -> Unit,
    onInfoClick: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    itemsIndexed(filterStates, key = { _, filter -> filter.type.name }) { index, filter ->
        AnimateOnEntry(
            delayMillis = (100 + (index * 50)).toLong()
        ) {
            Box(
                modifier = modifier.padding(horizontal = AppSpacing.lg)
            ) {
                FilterCard(
                    filterState = filter,
                    onEnabledChange = { onFilterToggle(filter.type, it) },
                    onRangeChange = { onRangeChange(filter.type, it) },
                    onInfoClick = { onInfoClick(filter.type) },
                    lastDrawNumbers = lastDraw
                )
            }
        }
    }
}

@Composable
fun GenerateActionsPanel(
    generationState: GenerationUiState,
    onGenerate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimateOnEntry(
        delayMillis = 300L,
        modifier = modifier.padding(horizontal = AppSpacing.lg)
    ) {
        Box {
            GenerationActionsPanel(
                generationState = generationState,
                onGenerate = onGenerate
            )
        }
    }
}
