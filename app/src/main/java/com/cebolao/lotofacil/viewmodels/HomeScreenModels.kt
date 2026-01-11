package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.Grid4x4
import androidx.compose.material.icons.outlined.LooksTwo
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.cebolao.lotofacil.data.StatisticsReport
import com.cebolao.lotofacil.domain.model.LastDrawStats

/**
 * Defines the different statistic patterns that can be visualised on the Home screen.
 * Each pattern has a user-visible title and an associated icon.
 */
enum class StatisticPattern(val title: String, val icon: ImageVector) {
    SUM("Soma", Icons.Outlined.Functions),
    EVENS("Pares", Icons.Outlined.LooksTwo),
    PRIMES("Primos", Icons.Outlined.Star),
    FRAME("Moldura", Icons.Outlined.Grid4x4),
    PORTRAIT("Miolo", Icons.Outlined.CropSquare),
    FIBONACCI("Fibonacci", Icons.Outlined.Timeline),
    MULTIPLES_OF_3("Múltiplos 3", Icons.Outlined.FormatListNumbered)
}

/**
 * Holds all state required by the Home screen, including loading and error flags, the
 * last draw statistics, computed summary statistics, and UI selections such as the
 * currently selected pattern and time window.
 */
@Stable
data class HomeUiState(
    val isScreenLoading: Boolean = true,
    val isStatsLoading: Boolean = false,
    @StringRes val errorMessageResId: Int? = null,
    val lastDrawStats: LastDrawStats? = null,
    val statistics: StatisticsReport? = null,
    val selectedPattern: StatisticPattern = StatisticPattern.SUM,
    val selectedTimeWindow: Int = 0,
    val showSyncFailedMessage: Boolean = false
)
