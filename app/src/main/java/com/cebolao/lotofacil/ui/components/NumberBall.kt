package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppConstants

@Stable
@Composable
fun NumberBall(
    number: Int,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    isDisabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val shape = MaterialTheme.shapes.medium
    
    // Memoize state values to prevent unnecessary recompositions
    val isSelectedState = rememberUpdatedState(isSelected)
    val isHighlightedState = rememberUpdatedState(isHighlighted)
    val isDisabledState = rememberUpdatedState(isDisabled)
    
    // Get colors directly from MaterialTheme without composable context
    val colors = MaterialTheme.colorScheme
    val (containerColor, contentColor, borderColor) = when {
        isDisabledState.value -> Triple(
            colors.onSurface.copy(alpha = 0.08f),
            colors.onSurface.copy(alpha = 0.30f),
            colors.outline.copy(alpha = 0.5f)
        )
        isSelectedState.value -> Triple(
            colors.primary,
            colors.onPrimary,
            colors.primary.copy(alpha = 0.8f)
        )
        isHighlightedState.value -> Triple(
            colors.primaryContainer,
            colors.primary,
            colors.primary.copy(alpha = 0.8f)
        )
        else -> Triple(
            colors.surfaceVariant,
            colors.onSurface,
            colors.outline.copy(alpha = 0.7f)
        )
    }
    
    // Pre-calculate semantics content outside composable context
    val stateResId = when {
        isSelectedState.value -> R.string.number_state_selected
        isHighlightedState.value -> R.string.number_state_highlighted
        isDisabledState.value -> R.string.number_state_disabled
        else -> R.string.number_state_available
    }

    val animatedContainerColor by animateColorAsState(
        targetValue = containerColor, 
        animationSpec = tween(AppConstants.ANIMATION_DURATION_NUMBER_BALL.toInt()), 
        label = "containerColor"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = contentColor, 
        animationSpec = tween(AppConstants.ANIMATION_DURATION_NUMBER_BALL.toInt()), 
        label = "contentColor"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor, 
        animationSpec = tween(AppConstants.ANIMATION_DURATION_NUMBER_BALL.toInt()), 
        label = "borderColor"
    )

    // Memoize border width calculation
    val borderWidth = remember(
        isSelectedState.value,
        isHighlightedState.value
    ) {
        if (isHighlightedState.value && !isSelectedState.value) 2.dp else 1.dp
    }

    // Memoize tonal elevation
    val tonalElevation = remember(isSelectedState.value) {
        if (isSelectedState.value) AppElevation.xs else AppElevation.none
    }

    Surface(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(
                width = borderWidth,
                color = animatedBorderColor,
                shape = shape
            )
            .then(
                if (onClick != null && !isDisabledState.value) {
                    Modifier.clickable { onClick?.invoke() }
                } else Modifier
            )
            .semantics {
                val stateText = when {
                    isSelectedState.value -> "selected"
                    isHighlightedState.value -> "highlighted"
                    isDisabledState.value -> "disabled"
                    else -> "available"
                }
                contentDescription = "Number $number, state: $stateText"
            },
        shape = shape,
        color = animatedContainerColor,
        tonalElevation = tonalElevation
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
