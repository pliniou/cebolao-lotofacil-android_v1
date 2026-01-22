package com.cebolao.lotofacil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cebolao.lotofacil.navigation.AppNavigation
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.bottomNavDestinations
import com.cebolao.lotofacil.navigation.navigateToDestination
import com.cebolao.lotofacil.ui.components.RetroBottomBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val haptic = LocalHapticFeedback.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarVisible by remember(currentDestination) {
        derivedStateOf {
            bottomNavDestinations.any { it.baseRoute == currentDestination?.route?.substringBefore('?') }
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                RetroBottomBar(
                    destinations = bottomNavDestinations,
                    currentDestination = currentDestination,
                    onDestinationClick = { destination: Destination ->
                        val isSelected = currentDestination
                            ?.hierarchy
                            ?.any { it.route?.startsWith(destination.baseRoute) == true } == true
                        if (!isSelected) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navController.navigateToDestination(destination)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onNavigateToGeneratedGames = {
                navController.navigateToDestination(Destination.GeneratedGames)
            }
        )
    }
}
