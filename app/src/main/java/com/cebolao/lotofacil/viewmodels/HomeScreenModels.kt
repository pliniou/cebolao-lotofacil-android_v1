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
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.R

/**
 * Defines the different statistic patterns that can be visualised on the Home screen.
 * Each pattern has a user-visible title and an associated icon.
 */
@Stable
enum class StatisticPattern(@StringRes val titleRes: Int, val icon: ImageVector) {
    SUM(R.string.sum_label, Icons.Outlined.Functions),
    EVENS(R.string.even_label, Icons.Outlined.LooksTwo),
    PRIMES(R.string.prime_label, Icons.Outlined.Star),
    FRAME(R.string.frame_label, Icons.Outlined.Grid4x4),
    PORTRAIT(R.string.portrait_label, Icons.Outlined.CropSquare),
    FIBONACCI(R.string.fibonacci_label, Icons.Outlined.Timeline),
    MULTIPLES_OF_3(R.string.multiples_of_3_label, Icons.Outlined.FormatListNumbered)
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
    val lastUpdateTime: String? = null
) {
    /**
     * Computed property to check if any data is available
     */
    val hasData: Boolean
        get() = lastDrawStats != null || statistics != null
    
    /**
     * Computed property to check if screen is in any loading state
     */
    val isLoading: Boolean
        get() = isScreenLoading || isStatsLoading
}

