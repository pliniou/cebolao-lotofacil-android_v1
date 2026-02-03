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
    val LOTOFACIL_NUMBER_RANGE = 1..25
    
    // Animation delays for staggered entry
    const val STAGGER_DELAY_MS = 60
    const val MAX_STAGGER_DELAY_MS = 500
    


    

    
    // File names
    const val HISTORY_ASSET_FILE = "lotofacil_resultados.txt"
}
