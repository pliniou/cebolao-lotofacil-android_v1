package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.LocalAppColors

@Composable
fun IconBadge(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    contentDescription: String? = null,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    tint: Color? = null
) {
    val colors = LocalAppColors.current
    val resolvedBackground = backgroundColor ?: colors.surface2
    val resolvedBorder = borderColor ?: colors.outline.copy(alpha = 0.9f)
    val resolvedTint = tint ?: colors.brandPrimary

    Box(
        modifier = modifier
            .size(size)
            .background(resolvedBackground, shape)
            .border(width = 1.dp, color = resolvedBorder, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        when {
            icon != null -> Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = resolvedTint,
                modifier = Modifier.size(iconSize)
            )
            painter != null -> Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = resolvedTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

