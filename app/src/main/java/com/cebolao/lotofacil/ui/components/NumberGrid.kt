package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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

@Composable
fun NumberGrid(
    modifier: Modifier = Modifier,
    items: List<NumberBallItem>,
    onNumberClick: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { it.key }
        ) { item ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = !item.isDisabled,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNumberClick(item.number)
                        }
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