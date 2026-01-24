package com.cebolao.lotofacil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cebolao.lotofacil.ui.screens.AboutScreen
import com.cebolao.lotofacil.ui.screens.CheckerScreen
import com.cebolao.lotofacil.ui.screens.FiltersScreen
import com.cebolao.lotofacil.ui.screens.GeneratedGamesScreen
import com.cebolao.lotofacil.ui.screens.HomeScreen

/**
 * Creates and hosts the app's navigation graph.  Each destination is
 * specified via a sealed interface [Destination].  Nested graphs can be
 * added using the `navigation` builder.
 */
@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Destination.Home.route) {
        composable(Destination.Home.route) {
            HomeScreen(
                onExploreFilters = { navController.navigate(Destination.Filters.route) },
                onOpenChecker = { navController.navigate(Destination.Checker.route) }
            )
        }
        composable(Destination.Filters.route) {
            FiltersScreen(
                onNavigateToGeneratedGames = { navController.navigate(Destination.GeneratedGames.route) }
            )
        }
        composable(Destination.GeneratedGames.route) { GeneratedGamesScreen() }
        composable(Destination.Checker.route) { CheckerScreen() }
        composable(Destination.About.route) { AboutScreen() }
    }
}
