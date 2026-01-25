package com.cebolao.lotofacil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cebolao.lotofacil.ui.screens.about.AboutScreen
import com.cebolao.lotofacil.ui.screens.checker.CheckerScreen
import com.cebolao.lotofacil.ui.screens.filters.FiltersScreen
import com.cebolao.lotofacil.ui.screens.generated.GeneratedGamesScreen
import com.cebolao.lotofacil.ui.screens.home.HomeScreen

/**
 * Navigation utilities for type-safe navigation.
 */
fun NavController.navigateToDestination(destination: Destination) {
    val route = when (destination) {
        is Destination.Home -> destination.route
        is Destination.Filters -> destination.route
        is Destination.GeneratedGames -> destination.route
        is Destination.Checker -> destination.baseRoute
        is Destination.About -> destination.route
    }

    navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
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
