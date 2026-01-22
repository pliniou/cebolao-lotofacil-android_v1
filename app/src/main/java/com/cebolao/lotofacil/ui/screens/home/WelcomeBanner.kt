package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.LocalAppColors
import com.cebolao.lotofacil.ui.theme.iconMedium

@Composable
fun WelcomeBanner(
    modifier: Modifier = Modifier,
    lastUpdateTime: String? = null,
    onExploreFilters: () -> Unit = {},
    onOpenChecker: () -> Unit = {}
) {
    val colors = LocalAppColors.current
    
    AnimateOnEntry(
        delayMillis = 50L,
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.lg),
            colors = CardDefaults.cardColors(
                containerColor = colors.brandSubtle
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.brandPrimary.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.AppRegistration,
                        contentDescription = null,
                        tint = colors.brandPrimary,
                        modifier = Modifier.size(iconMedium())
                    )
                }
                
                Text(
                    text = stringResource(id = R.string.welcome_banner_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = stringResource(id = R.string.welcome_banner_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
                
                lastUpdateTime?.let { time ->
                    Text(
                        text = stringResource(id = R.string.last_update_status, time),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textTertiary
                    )
                }
                
                Row(
                    modifier = Modifier.padding(top = AppSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    QuickActionChip(
                        text = stringResource(id = R.string.welcome_tip_filters),
                        icon = Icons.Default.Tune,
                        onClick = onExploreFilters,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionChip(
                        text = stringResource(id = R.string.welcome_tip_checker),
                        icon = Icons.Default.CheckCircle,
                        onClick = onOpenChecker,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    
    androidx.compose.material3.AssistChip(
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                color = colors.textPrimary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconMedium()),
                tint = colors.brandPrimary
            )
        },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = colors.surface1,
            labelColor = colors.textPrimary,
            leadingIconContentColor = colors.brandPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = colors.outline
        )
    )
}
