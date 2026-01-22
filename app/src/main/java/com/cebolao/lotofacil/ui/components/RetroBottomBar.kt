package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.LocalAppColors

@Composable
fun RetroBottomBar(
    destinations: List<Destination>,
    currentDestination: NavDestination?,
    onDestinationClick: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface1,
        tonalElevation = 0.dp
    ) {
        Column {
            HorizontalDivider(color = colors.outline.copy(alpha = 0.8f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { destination ->
                    val isSelected = currentDestination
                        ?.hierarchy
                        ?.any { it.route?.startsWith(destination.baseRoute) == true } == true

                    val targetContainer = if (isSelected) colors.brandSubtle else colors.surface2
                    val containerColor by animateColorAsState(
                        targetValue = targetContainer,
                        animationSpec = tween(durationMillis = 160),
                        label = "bottomBarContainer"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) colors.brandPrimary else colors.textSecondary,
                        animationSpec = tween(durationMillis = 160),
                        label = "bottomBarContent"
                    )

                    val shape = MaterialTheme.shapes.medium
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .background(containerColor, shape)
                            .border(1.dp, colors.outline.copy(alpha = 0.85f), shape)
                            .clickable(
                                role = Role.Tab,
                                onClick = { onDestinationClick(destination) }
                            )
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.title,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = destination.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

