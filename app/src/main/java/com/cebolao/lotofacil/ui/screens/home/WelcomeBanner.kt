package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun WelcomeBanner(
    modifier: Modifier = Modifier,
    lastUpdateTime: String? = null,
    onExploreFilters: () -> Unit = {},
    onOpenChecker: () -> Unit = {}
) {
    AnimateOnEntry(
        delayMillis = 50L,
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.lg),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.emoji_lightbulb),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = stringResource(id = R.string.welcome_banner_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = stringResource(id = R.string.welcome_banner_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                lastUpdateTime?.let { time ->
                    Text(
                        text = stringResource(id = R.string.last_update_status, time),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
    androidx.compose.material3.AssistChip(
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            labelColor = MaterialTheme.colorScheme.primary,
            leadingIconContentColor = MaterialTheme.colorScheme.primary
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    )
}
