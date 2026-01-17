package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.LocalAppColors

@Composable
fun StandardScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val colors = LocalAppColors.current
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.background,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = AppSpacing.lg,
                vertical = AppSpacing.xl
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    if (icon != null || iconPainter != null) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    colors.surface2,
                                    CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = colors.outline,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (icon != null) {
                                androidx.compose.material3.Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = colors.brandPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else if (iconPainter != null) {
                                androidx.compose.material3.Icon(
                                    painter = iconPainter,
                                    contentDescription = null,
                                    tint = colors.brandPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
                
                actions?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                        content = it
                    )
                }
            }
        }
    }
}
