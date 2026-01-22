package com.cebolao.lotofacil.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.IconBadge
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.LocalAppColors

/**
 * Reusable, flat card for statistics blocks with a consistent header layout.
 */
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalAppColors.current

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = colors.surface1,
        elevation = AppCardDefaults.elevation
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                if (icon != null) {
                    IconBadge(
                        icon = icon,
                        contentDescription = null,
                        size = 36.dp,
                        iconSize = 18.dp
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
            }

            HorizontalDivider(color = colors.outline.copy(alpha = 0.6f))

            content()
        }
    }
}
