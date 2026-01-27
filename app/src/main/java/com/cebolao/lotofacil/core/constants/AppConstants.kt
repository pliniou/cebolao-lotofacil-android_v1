package com.cebolao.lotofacil.core.constants

/**
 * Application-wide constants for magic numbers and configuration values.
 */
object AppConstants {
    // Animation durations in milliseconds
    const val ANIMATION_DURATION_SHORT = 100L
    const val ANIMATION_DURATION_MEDIUM = 250L
    const val ANIMATION_DURATION_LONG = 400L
    const val ANIMATION_DURATION_SPLASH = 1300L
    
    // Game generation
    const val GAME_SIZE = 15
    const val MAX_GAME_GENERATION_ATTEMPTS = 250_000
    val LOTOFACIL_NUMBER_RANGE = 1..25
    const val MAX_FILTERS_PER_GAME = 8
    
    // Animation delays for staggered entry
    const val STAGGER_DELAY_MS = 60
    const val MAX_STAGGER_DELAY_MS = 500
    
    // UI timeouts
    const val SNACKBAR_DURATION_LONG = 4000L
    const val SNACKBAR_DURATION_SHORT = 2000L
    
    // Database
    const val DATABASE_NAME = "lotofacil_database"
    const val DATABASE_VERSION = 1
    
    // Network
    const val NETWORK_TIMEOUT_SECONDS = 30L
    
    // Game checking
    const val CHECK_PROGRESS_INCREMENT = 0.1f
    
    // Statistics
    const val DEFAULT_STATISTICS_TIME_WINDOW = 100
    const val MIN_STATISTICS_TIME_WINDOW = 10
    
    // Game analysis
    const val ANALYSIS_TIMEOUT_MS = 30_000L
    
    // File names
    const val HISTORY_ASSET_FILE = "lotofacil_resultados.txt"
}
