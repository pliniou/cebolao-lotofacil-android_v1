package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cebolao.lotofacil.navigation.AppNavigation
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.bottomNavDestinations
import com.cebolao.lotofacil.navigation.navigateToDestination
import com.cebolao.lotofacil.ui.components.AppBottomBar

/**
 * Displays the main screen with optimized performance.
 * Uses Scaffold with bottom navigation and proper content padding.
 * Modernized with consistent spacing and elevation.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    // Handle splash screen dismissal
    if (!uiState.isReady) {
        SplashScreen()
        return
    }

    val haptic = LocalHapticFeedback.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AppBottomBar(
                destinations = bottomNavDestinations,
                selectedDestination = navController.currentBackStackEntry?.destination?.route?.let { route ->
                    bottomNavDestinations.find { it::class.qualifiedName == route }
                } ?: bottomNavDestinations.first(),
                onDestinationSelected = { destination ->
                    val isSelected = currentDestination
                        ?.hierarchy
                        ?.any { it.hasRoute(destination::class) } == true
                    if (!isSelected) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigateToDestination(destination)
                    }
                }
            )
        }
    ) { paddingValues ->
        AppNavigation(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onNavigateToGeneratedGames = {
                navController.navigateToDestination(Destination.GeneratedGames)
            }
        )
    }
}
