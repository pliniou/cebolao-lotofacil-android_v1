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
import com.cebolao.lotofacil.ui.screens.insights.FrequencyAnalysisScreen
import com.cebolao.lotofacil.ui.screens.stats.UserStatsScreen

/**
 * Navigation utilities for type-safe navigation.
 */
fun NavController.navigateToDestination(destination: Destination) {
    navigate(destination) {
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
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Destination.Home> {
            HomeScreen(
                onExploreFilters = {
                    navController.navigateToDestination(Destination.Filters)
                },
                onOpenChecker = {
                    navController.navigateToDestination(Destination.Checker())
                },
                onNavigateToInsights = {
                    navController.navigateToDestination(Destination.Insights)
                }
            )
        }
        
        composable<Destination.Filters> {
            FiltersScreen {
                onNavigateToGeneratedGames()
            }
        }
        
        composable<Destination.GeneratedGames> {
            GeneratedGamesScreen()
        }
        
        composable<Destination.Checker> {
            // Arguments are automatically handled by the Destination.Checker data class
            // parsing if we need them, but here we just show the screen.
            // If the screen needs args, we can obtain them via:
            // val args = it.toRoute<Destination.Checker>()
            CheckerScreen()
        }
        
        composable<Destination.About> {
            AboutScreen(onNavigateToUserStats = { navController.navigate(Destination.UserStats) })
        }

        composable<Destination.Insights> {
            FrequencyAnalysisScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Destination.UserStats> {
            UserStatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
