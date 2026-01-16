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
import androidx.compose.ui.platform.LocalHapticFeedback
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberGrid(
    modifier: Modifier = Modifier,
    items: List<NumberBallItem>,
    onNumberClick: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    // Optimize by caching the click handler
    val handleClick = remember { { number: Int ->
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onNumberClick(number)
    } }

    // Using FlowRow instead of LazyVerticalGrid to avoid nested scrolling issues
    // and infinite height constraints within LazyColumn.
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 5
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = !item.isDisabled,
                        onClick = { handleClick(item.number) }
                    )
                    .padding(2.dp)
            ) {
                NumberBall(
                    number = item.number,
                    isSelected = item.isSelected,
                    isDisabled = item.isDisabled,
                    size = 40.dp,
                    variant = NumberBallVariant.Primary
                )
            }
        }
    }
}