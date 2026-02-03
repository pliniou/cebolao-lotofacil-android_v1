package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppElevation

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    elevation: Dp = AppCardDefaults.elevation,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val cardColors = if (backgroundColor != null || contentColor != null) {
        CardDefaults.elevatedCardColors(
            containerColor = backgroundColor ?: colors.surface,
            contentColor = contentColor ?: colors.onSurface
        )
    } else {
        CardDefaults.elevatedCardColors()
    }

    androidx.compose.material3.ElevatedCard(
        modifier = modifier,
        shape = shape,
        colors = cardColors,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation,
            hoveredElevation = AppCardDefaults.hoverElevation
        )
    ) {
        content()
    }
}

@Composable
fun ClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    elevation: Dp = AppCardDefaults.elevation,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val cardColors = if (backgroundColor != null || contentColor != null) {
        CardDefaults.elevatedCardColors(
            containerColor = backgroundColor ?: colors.surface,
            contentColor = contentColor ?: colors.onSurface
        )
    } else {
        CardDefaults.elevatedCardColors()
    }
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    val cardModifier = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(shape)
        .then(if (border != null) Modifier.border(border, shape) else Modifier)

    androidx.compose.material3.ElevatedCard(
        modifier = cardModifier,
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = cardColors,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation,
            pressedElevation = if (isPressed) AppElevation.none else elevation,
            hoveredElevation = AppCardDefaults.hoverElevation
        )
    ) {
        content()
    }
}

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    tonalElevation: Dp = AppElevation.none,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = shape,
        tonalElevation = tonalElevation,
        color = colors.surfaceVariant
    ) {
        content()
    }
}
