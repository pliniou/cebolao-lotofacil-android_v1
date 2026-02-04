package com.cebolao.lotofacil.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Constants used throughout the application for animations, delays, and other UI behaviors.
 */
object AppConstants {
    
    // Animation durations in milliseconds
    const val ANIMATION_DURATION_NUMBER_BALL: Long = 200L
    const val ANIMATION_DURATION_ELEVATION: Long = 150L
    const val ANIMATION_DURATION_COLOR_CHANGE: Long = 300L
    const val ANIMATION_DURATION_FADE: Long = 250L
    const val ANIMATION_DURATION_SCALE: Long = 180L
    const val ANIMATION_DURATION_FILTER_PANEL: Long = 200L
    const val ANIMATION_DURATION_PROBABILITY: Long = 300L
    const val ANIMATION_DURATION_SCORE_COUNT: Long = 250L
    const val ANIMATION_DURATION_SHIMMER: Long = 1200L
    
    // Animation delays in milliseconds
    const val ANIMATION_DELAY_ENTRY: Long = 100L
    const val ANIMATION_DELAY_CHECKER: Long = 150L
    const val ANIMATION_DELAY_FILTERS: Long = 120L
    
    // Spacing constants
    val DEFAULT_PADDING: Dp = 16.dp
    val SMALL_PADDING: Dp = 8.dp
    val LARGE_PADDING: Dp = 24.dp
    
    // Animation easing constants
    const val DEFAULT_EASING_FACTOR = 0.4f
    const val BOUNCY_EASING_FACTOR = 0.6f
}
