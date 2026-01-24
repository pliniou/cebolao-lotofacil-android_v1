package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.ui.theme.AppElevation


enum class NumberBallVariant {
    Primary, Secondary, Lotofacil
}

@Composable
fun NumberBall(
    number: Int,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    isDisabled: Boolean = false,
    variant: NumberBallVariant = NumberBallVariant.Primary
) {
    val shape = MaterialTheme.shapes.medium
    val (containerColor, contentColor, borderColor) = getBallColors(
        isSelected = isSelected,
        isHighlighted = isHighlighted,
        isDisabled = isDisabled
    )

    val animatedContainerColor by animateColorAsState(containerColor, tween(200), label = "containerColor")
    val animatedContentColor by animateColorAsState(contentColor, tween(200), label = "contentColor")
    val animatedBorderColor by animateColorAsState(borderColor, tween(200), label = "borderColor")

    Surface(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(
                width = if (isHighlighted && !isSelected) 2.dp else 1.dp,
                color = animatedBorderColor,
                shape = shape
            )
            .semantics {
                val state = when {
                    isSelected -> "selecionado"
                    isHighlighted -> "destacado"
                    isDisabled -> "desabilitado"
                    else -> "disponível"
                }
                contentDescription = "Número $number, $state"
            },
        shape = shape,
        color = animatedContainerColor,
        tonalElevation = if (isSelected) AppElevation.sm else AppElevation.none
    ) {
        val formattedNumber = remember(number) { "%02d".format(number) }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = formattedNumber,
                color = animatedContentColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (size.value / 2.6).sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun getBallColors(
    isSelected: Boolean,
    isHighlighted: Boolean,
    isDisabled: Boolean
): Triple<Color, Color, Color> {
    val colors = MaterialTheme.colorScheme

    return when {
        isDisabled -> Triple(
            colors.onSurface.copy(alpha = 0.12f),
            colors.onSurface.copy(alpha = 0.38f),
            colors.outline.copy(alpha = 0.6f)
        )
        isSelected -> Triple(
            colors.primary,
            colors.onPrimary,
            colors.primary.copy(alpha = 0.9f)
        )
        isHighlighted -> Triple(
            colors.primaryContainer,
            colors.primary,
            colors.primary.copy(alpha = 0.9f)
        )
        else -> Triple(
            colors.surfaceVariant,
            colors.onSurface,
            colors.outline.copy(alpha = 0.75f)
        )
    }
}

