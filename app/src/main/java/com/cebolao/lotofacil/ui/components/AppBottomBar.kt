package com.cebolao.lotofacil.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.selectedIcon
import com.cebolao.lotofacil.navigation.titleRes
import com.cebolao.lotofacil.navigation.unselectedIcon

/**
 * Custom bottom navigation bar with modern styling and optimized interactions.
 * Uses Material3 NavigationBar with consistent elevation and spacing.
 */
@Composable
fun AppBottomBar(
    destinations: List<Destination>,
    selectedDestination: Destination,
    onDestinationSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current

    NavigationBar(
        modifier = modifier,
        containerColor = colors.surface,
        tonalElevation = 3.dp
    ) {
        destinations.forEach { destination ->
            val selected = destination == selectedDestination
            
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDestinationSelected(destination)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.titleRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.titleRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    selectedTextColor = colors.primary,
                    unselectedIconColor = colors.onSurfaceVariant,
                    unselectedTextColor = colors.onSurfaceVariant,
                    indicatorColor = colors.primaryContainer
                )
            )
        }
    }
}
