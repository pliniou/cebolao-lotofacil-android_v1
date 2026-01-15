package com.cebolao.lotofacil.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun OptimizedNumberBall(
    number: Int,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    isDisabled: Boolean = false,
    variant: NumberBallVariant = NumberBallVariant.Primary
) {
    NumberBall(
        number = number,
        modifier = modifier,
        size = size,
        isSelected = isSelected,
        isHighlighted = isHighlighted,
        isDisabled = isDisabled,
        variant = variant
    )
}
