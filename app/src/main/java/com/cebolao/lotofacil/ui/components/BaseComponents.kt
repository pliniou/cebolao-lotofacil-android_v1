package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.LocalAppColors

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    elevation: Dp = AppCardDefaults.elevation,
    content: @Composable () -> Unit
) {
    val colors = LocalAppColors.current
    val cardBackgroundColor = backgroundColor ?: colors.surface1
    val cardContentColor = contentColor ?: colors.textPrimary
    
    val defaultBorder = BorderStroke(
        width = 1.dp,
        color = colors.outline.copy(alpha = 0.75f)
    )

    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            contentColor = cardContentColor
        ),
        border = border ?: defaultBorder,
        elevation = CardDefaults.cardElevation(elevation)
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
    val colors = LocalAppColors.current
    val cardBackgroundColor = backgroundColor ?: colors.surface1
    val cardContentColor = contentColor ?: colors.textPrimary
    
    val defaultBorder = BorderStroke(
        width = 1.dp,
        color = colors.outline.copy(alpha = 0.75f)
    )
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            contentColor = cardContentColor
        ),
        border = border ?: defaultBorder,
        elevation = CardDefaults.cardElevation(if (isPressed) elevation / 2 else elevation)
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
    val colors = LocalAppColors.current
    
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = shape,
        tonalElevation = tonalElevation,
        color = colors.surface2
    ) {
        content()
    }
}
