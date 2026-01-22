@file:Suppress("ConstPropertyName")

package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
data class NumberBallItem(
    val number: Int,
    val isSelected: Boolean,
    val isDisabled: Boolean,
    override val key: Any = number
) : StableKey

@Stable
interface StableKey {
    val key: Any
}

object NumberGridDimens {
    val baseBallSize: Dp = 40.dp
    val compactBallSize: Dp = 36.dp
    val expandedBallSize: Dp = 44.dp
    val spacing: Dp = 8.dp
    const val maxItemsPerRow = 5
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberGrid(
    modifier: Modifier = Modifier,
    items: List<NumberBallItem>,
    onNumberClick: (Int) -> Unit,
    ballSize: Dp = NumberGridDimens.baseBallSize
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val screenDensity = density.density

    val adaptiveBallSize = when {
        screenDensity < 1.5f -> NumberGridDimens.compactBallSize
        screenDensity > 2.5f -> NumberGridDimens.expandedBallSize
        else -> ballSize
    }

    // Optimize by caching the click handler and using stable keys
    val handleClick = remember(onNumberClick) { { number: Int ->
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onNumberClick(number)
    } }

    // Using FlowRow instead of LazyVerticalGrid to avoid nested scrolling issues
    // and infinite height constraints within LazyColumn.
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(NumberGridDimens.spacing)
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(NumberGridDimens.spacing, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(NumberGridDimens.spacing),
        maxItemsInEachRow = NumberGridDimens.maxItemsPerRow
    ) {
        for (item in items) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = !item.isDisabled,
                        onClick = { handleClick(item.number) }
                    )
                    .padding(2.dp)
                    .semantics {
                        contentDescription = "Número ${item.number}${if (item.isSelected) " selecionado" else ""}${if (item.isDisabled) " desabilitado" else ""}"
                    }
            ) {
                NumberBall(
                    number = item.number,
                    isSelected = item.isSelected,
                    isDisabled = item.isDisabled,
                    size = adaptiveBallSize,
                    variant = NumberBallVariant.Primary
                )
            }
        }
    }
}