package com.cebolao.lotofacil.ui.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.NumberBallVariant
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameCard(
    game: LotofacilGame,
    modifier: Modifier = Modifier,
    onAnalyzeClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    pinIcon: ImageVector = Icons.Default.PushPin,
    pinIconOutlined: ImageVector = Icons.Outlined.PushPin,
    deleteIcon: ImageVector = Icons.Default.Delete,
    analyzeIcon: ImageVector = Icons.Default.Analytics
) {
    val haptic = LocalHapticFeedback.current
    
    // Optimize expensive calculations with derivedStateOf
    val sortedNumbers by remember(game.numbers) {
        derivedStateOf { game.numbers.sorted() }
    }
    
    val isPinned = game.isPinned

    val elevation by animateDpAsState(
        if (isPinned) AppCardDefaults.pinnedElevation else AppCardDefaults.elevation, 
        spring(stiffness = Spring.StiffnessMedium), 
        label = "elevation"
    )
    val borderColor by animateColorAsState(
        if (isPinned) MaterialTheme.colorScheme.primary else Color.Transparent, 
        tween(250), 
        label = "borderColor"
    )
    val containerColor by animateColorAsState(
        if (isPinned) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        tween(250), 
        label = "containerColor"
    )

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = containerColor,
        border = BorderStroke(0.5.dp, borderColor.copy(alpha = 0.5f)),
        elevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                maxItemsInEachRow = 5
            ) {
                for (number in sortedNumbers) {
                    NumberBall(
                        number = number, 
                        size = 38.dp, 
                        variant = NumberBallVariant.Secondary
                    )
                }
            }
            GameCardActions(
                isPinned = isPinned,
                onAnalyzeClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onAnalyzeClick()
                },
                onPinClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPinClick()
                },
                onDeleteClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDeleteClick()
                },
                pinIcon = pinIcon,
                pinIconOutlined = pinIconOutlined,
                deleteIcon = deleteIcon,
                analyzeIcon = analyzeIcon
            )
        }
    }
}

@Composable
private fun GameCardActions(
    isPinned: Boolean,
    onAnalyzeClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    pinIcon: ImageVector,
    pinIconOutlined: ImageVector,
    deleteIcon: ImageVector,
    analyzeIcon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            IconButton(
                onClick = onPinClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isPinned) pinIcon else pinIconOutlined,
                    contentDescription = if (isPinned) stringResource(id = R.string.unpin_game) else stringResource(id = R.string.pin_game),
                    tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    deleteIcon, 
                    stringResource(id = R.string.delete_game), 
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        TextButton(onClick = onAnalyzeClick) {
            Icon(
                analyzeIcon, 
                null, 
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(id = R.string.analyze_button))
        }
    }
}
