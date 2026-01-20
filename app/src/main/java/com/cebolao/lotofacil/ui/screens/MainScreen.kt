package com.cebolao.lotofacil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cebolao.lotofacil.navigation.AppNavigation
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.navigateToDestination
import com.cebolao.lotofacil.navigation.bottomNavDestinations

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarVisible by remember(currentDestination) {
        derivedStateOf {
            bottomNavDestinations.any { it.baseRoute == currentDestination?.route?.substringBefore('?') }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                NavigationBar {
                    for (destination in bottomNavDestinations) {
                        val isSelected = currentDestination?.hierarchy?.any { it.route?.startsWith(destination.baseRoute) == true } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                navController.navigateToDestination(destination)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    destination.title,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors()
                        )
                    }
                }
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
