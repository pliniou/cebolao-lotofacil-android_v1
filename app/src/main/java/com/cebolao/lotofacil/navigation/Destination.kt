package com.cebolao.lotofacil.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations that encapsulate routes and arguments.
 * This replaces string-based navigation with compile-time safety.
 */
@Stable
sealed interface Destination {
    val route: String
    val title: String
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
    val baseRoute: String get() = route.substringBefore('?')

    @Serializable
    data object Home : Destination {
        override val route = "home"
        override val title = "Início"
        override val selectedIcon = Icons.Filled.Home
        override val unselectedIcon = Icons.Outlined.Home
    }

    @Serializable
    data object Filters : Destination {
        override val route = "filters"
        override val title = "Gerador"
        override val selectedIcon = Icons.Filled.Tune
        override val unselectedIcon = Icons.Outlined.Tune
    }

    @Serializable
    data object GeneratedGames : Destination {
        override val route = "generated_games"
        override val title = "Jogos"
        override val selectedIcon = Icons.AutoMirrored.Filled.ListAlt
        override val unselectedIcon = Icons.AutoMirrored.Filled.ListAlt
    }

    @Serializable
    data object Checker : Destination {
        const val NUMBERS_ARG = "numbers"
        override val route = "checker?$NUMBERS_ARG={$NUMBERS_ARG}"
        override val title = "Conferidor"
        override val selectedIcon = Icons.Filled.Analytics
        override val unselectedIcon = Icons.Outlined.Analytics
        
        val arguments = listOf(
            navArgument(NUMBERS_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    }

    @Serializable
    data object About : Destination {
        override val route = "about"
        override val title = "Sobre"
        override val selectedIcon = Icons.Filled.Info
        override val unselectedIcon = Icons.Outlined.Info
    }
}

val bottomNavDestinations = listOf(
    Destination.Home,
    Destination.Filters,
    Destination.GeneratedGames,
    Destination.Checker,
    Destination.About
)
