package com.cebolao.lotofacil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cebolao.lotofacil.ui.screens.AboutScreen
import com.cebolao.lotofacil.ui.screens.CheckerScreen
import com.cebolao.lotofacil.ui.screens.FiltersScreen
import com.cebolao.lotofacil.ui.screens.GeneratedGamesScreen
import com.cebolao.lotofacil.ui.screens.HomeScreen

/**
 * Navigation utilities for type-safe navigation.
 */
fun NavController.navigateToDestination(destination: Destination) {
    when (destination) {
        is Destination.Home -> navigate(destination.route) {
            popUpTo(graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        is Destination.Filters -> navigate(destination.route) {
            popUpTo(graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        is Destination.GeneratedGames -> navigate(destination.route) {
            popUpTo(graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        is Destination.Checker -> navigate(destination.baseRoute)
        is Destination.About -> navigate(destination.route)
    }
}

/**
 * Navigation graph setup using type-safe destinations.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: androidx.navigation.NavHostController,
    startDestination: Destination = Destination.Home,
    onNavigateToGeneratedGames: () -> Unit = {}
) {
    
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Destination.Home.route) {
            HomeScreen(
                onExploreFilters = {
                    navController.navigateToDestination(Destination.Filters)
                },
                onOpenChecker = {
                    navController.navigateToDestination(Destination.Checker)
                }
            )
        }
        
        composable(Destination.Filters.route) {
            FiltersScreen {
                onNavigateToGeneratedGames()
            }
        }
        
        composable(Destination.GeneratedGames.route) {
            GeneratedGamesScreen()
        }
        
        composable(
            route = Destination.Checker.route,
            arguments = Destination.Checker.arguments
        ) {
            CheckerScreen()
        }
        
        composable(Destination.About.route) {
            AboutScreen()
        }
    }
}
