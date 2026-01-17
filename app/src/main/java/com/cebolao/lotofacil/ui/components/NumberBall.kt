package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.cebolao.lotofacil.ui.theme.LocalAppColors

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
    val (containerColor, contentColor, borderColor) = getBallColors(
        isSelected = isSelected,
        isHighlighted = isHighlighted,
        isDisabled = isDisabled,
        variant = variant
    )

    val animatedContainerColor by animateColorAsState(containerColor, tween(250), label = "containerColor")
    val animatedContentColor by animateColorAsState(contentColor, tween(250), label = "contentColor")
    val animatedBorderColor by animateColorAsState(borderColor, tween(250), label = "borderColor")

    Surface(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(
                width = if (isHighlighted && !isSelected) 1.5.dp else 0.5.dp,
                color = animatedBorderColor,
                shape = CircleShape
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
        shape = CircleShape,
        color = animatedContainerColor,
        tonalElevation = if (isSelected) AppElevation.sm else AppElevation.none
    ) {
        val formattedNumber = remember(number) { "%02d".format(number) }
        
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formattedNumber,
                color = animatedContentColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (size.value / 2.6).sp, // Slightly larger font for better legibility
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
    isDisabled: Boolean,
    variant: NumberBallVariant
): Triple<Color, Color, Color> {
    val colors = LocalAppColors.current
    
    return when {
        isDisabled -> Triple(
            colors.disabledContainer,
            colors.disabledContent,
            colors.outline.copy(alpha = 0.3f)
        )
        isSelected -> Triple(
            colors.brandPrimary,
            colors.background,
            colors.brandPrimary.copy(alpha = 0.3f)
        )
        isHighlighted -> Triple(
            colors.brandSubtle,
            colors.brandPrimary,
            colors.brandPrimary.copy(alpha = 0.7f)
        )
        else -> Triple(
            colors.surface2,
            colors.textPrimary,
            colors.outline.copy(alpha = 0.2f)
        )
    }
}