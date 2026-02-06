package com.cebolao.lotofacil.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Standardized size values for components across the application.
 * Provides a single source of truth for all component dimensions.
 */
object AppSize {
    
    // Number ball sizes
    val numberBallSmall = 38.dp
    val numberBallMedium = 48.dp
    val numberBallLarge = 56.dp
    
    // Chart dimensions
    val chartHeightDefault = 240.dp
    val chartHeightSmall = 180.dp
    val chartHeightLarge = 300.dp
    
    // Card and container sizes
    val cardCornerSmall = 8.dp
    val cardCornerMedium = 12.dp
    val cardCornerLarge = 16.dp
    
    // Icon sizes (matches Material Design guidelines)
    val iconSmall = 20.dp      // Xs
    val iconSmallMedium = 22.dp // Sm
    val iconMedium = 24.dp      // Md
    val iconMediumLarge = 28.dp // Lg
    val iconLarge = 32.dp       // Xl
    val iconExtraLarge = 48.dp  // 2xl
    
    // Touch target minimum size (Material Design accessibility)
    val touchTargetMinimum = 48.dp
    
    // Button dimensions
    val buttonHeightDefault = 48.dp
    val buttonHeightSmall = 40.dp
    val buttonHeightLarge = 56.dp
    
    // Divider and separator heights
    val dividerThickness = 1.dp
    val dividerThicknessBold = 2.dp
    
    // Progress indicator sizes
    val progressIndicatorSmall = 24.dp
    val progressIndicatorMedium = 40.dp
    val progressIndicatorLarge = 56.dp
    
    // Badge and chip sizes
    val chipHeight = 32.dp
    val badgeSize = 24.dp
}
