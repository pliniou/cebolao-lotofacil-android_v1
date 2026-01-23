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
import androidx.compose.ui.res.stringResource
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
        tonalElevation = 3.dp,
        shadowElevation = 8.dp 
    ) {
        Column {
            // Top border for separation
            HorizontalDivider(thickness = 1.dp, color = colors.outline)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { destination ->
                    val isSelected = currentDestination
                        ?.hierarchy
                        ?.any { it.route?.startsWith(destination.baseRoute) == true } == true

                    // Flat Retro Design: Solid high-contrast selection
                    val containerColor = if (isSelected) colors.brandPrimary else androidx.compose.ui.graphics.Color.Transparent
                    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else colors.textSecondary
                    val borderColor = if (isSelected) colors.brandSecondary else androidx.compose.ui.graphics.Color.Transparent
                    
                    val shape = MaterialTheme.shapes.small // Slightly rounded rects

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .background(containerColor, shape)
                            .border(if(isSelected) 1.dp else 0.dp, borderColor, shape) // Subtle depth border
                            .clickable(
                                role = Role.Tab,
                                onClick = { onDestinationClick(destination) }
                            )
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = stringResource(destination.titleRes),
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        if (isSelected) {
                            Text(
                                text = stringResource(destination.titleRes),
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
}

