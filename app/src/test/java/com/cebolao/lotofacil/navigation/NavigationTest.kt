package com.cebolao.lotofacil.navigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class NavigationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `bottom nav destinations list contains expected items`() {
        val expectedCount = 5  // Home, Filters, GeneratedGames, Checker, About
        assertEquals(expectedCount, bottomNavDestinations.size)
    }

    @Test
    fun `each destination has valid title resource`() = runTest {
        val destinations = listOf(
            Destination.Home,
            Destination.Filters,
            Destination.GeneratedGames,
            Destination.Checker(),
            Destination.About,
            Destination.Insights,
            Destination.UserStats
        )

        destinations.forEach { destination ->
            assert(destination.titleRes > 0, "Invalid titleRes for $destination")
        }
    }

    @Test
    fun `each destination has selected icon`() = runTest {
        val destinations = listOf(
            Destination.Home,
            Destination.Filters,
            Destination.GeneratedGames,
            Destination.Checker(),
            Destination.About,
            Destination.Insights,
            Destination.UserStats
        )

        destinations.forEach { destination ->
            val icon = destination.selectedIcon
            assertNotNull(icon, "Missing selectedIcon for $destination")
        }
    }

    @Test
    fun `each destination has unselected icon`() = runTest {
        val destinations = listOf(
            Destination.Home,
            Destination.Filters,
            Destination.GeneratedGames,
            Destination.Checker(),
            Destination.About,
            Destination.Insights,
            Destination.UserStats
        )

        destinations.forEach { destination ->
            val icon = destination.unselectedIcon
            assertNotNull(icon, "Missing unselectedIcon for $destination")
        }
    }

    @Test
    fun `checker destination preserves numbers parameter`() {
        val numbers = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"
        val destination = Destination.Checker(numbers)
        assertEquals(numbers, destination.numbers)
    }

    @Test
    fun `checker destination default numbers is null`() {
        val destination = Destination.Checker()
        assert(destination.numbers == null)
    }

    private fun assertNotNull(value: Any?, message: String) {
        assert(value != null) { message }
    }
}
