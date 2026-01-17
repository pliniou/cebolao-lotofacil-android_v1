package com.cebolao.lotofacil.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp

data class AppColors(
    // Base Colors
    val background: androidx.compose.ui.graphics.Color,
    val surface1: androidx.compose.ui.graphics.Color,
    val surface2: androidx.compose.ui.graphics.Color,
    val surface3: androidx.compose.ui.graphics.Color,
    val outline: androidx.compose.ui.graphics.Color,
    
    // Text Colors
    val textPrimary: androidx.compose.ui.graphics.Color,
    val textSecondary: androidx.compose.ui.graphics.Color,
    val textTertiary: androidx.compose.ui.graphics.Color,
    
    // Brand Colors
    val brandPrimary: androidx.compose.ui.graphics.Color,
    val brandSecondary: androidx.compose.ui.graphics.Color,
    val brandSubtle: androidx.compose.ui.graphics.Color,
    
    // Status Colors
    val success: androidx.compose.ui.graphics.Color,
    val warning: androidx.compose.ui.graphics.Color,
    val error: androidx.compose.ui.graphics.Color,
    
    // Disabled States
    val disabledContainer: androidx.compose.ui.graphics.Color,
    val disabledContent: androidx.compose.ui.graphics.Color
)

val LightAppColors = AppColors(
    background = LightBackground,
    surface1 = LightSurface,
    surface2 = LightSurfaceVariant,
    surface3 = LightSurfaceVariant,
    outline = LightOutline,
    textPrimary = LightOnBackground,
    textSecondary = LightOnSurfaceVariant,
    textTertiary = LightOutline,
    brandPrimary = LightPrimary,
    brandSecondary = LightSecondary,
    brandSubtle = LightPrimaryContainer,
    success = Success,
    warning = Warning,
    error = LightError,
    disabledContainer = LightSurfaceVariant,
    disabledContent = LightOutline
)

val DarkAppColors = AppColors(
    background = BaseBackground,
    surface1 = Surface1,
    surface2 = Surface2,
    surface3 = Surface3,
    outline = OutlineStroke,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    brandPrimary = BrandPrimary,
    brandSecondary = BrandSecondary,
    brandSubtle = BrandSubtle,
    success = Success,
    warning = Warning,
    error = Error,
    disabledContainer = DisabledContainer,
    disabledContent = DisabledContent
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

data class Spacing(
    val xs: androidx.compose.ui.unit.Dp = 4.dp,
    val sm: androidx.compose.ui.unit.Dp = 8.dp,
    val md: androidx.compose.ui.unit.Dp = 12.dp,
    val lg: androidx.compose.ui.unit.Dp = 16.dp,
    val xl: androidx.compose.ui.unit.Dp = 20.dp,
    val xxl: androidx.compose.ui.unit.Dp = 24.dp,
    val xxxl: androidx.compose.ui.unit.Dp = 32.dp
)

val AppSpacing = Spacing()

data class Elevation(
    val none: androidx.compose.ui.unit.Dp = 0.dp,
    val xs: androidx.compose.ui.unit.Dp = 1.dp,
    val sm: androidx.compose.ui.unit.Dp = 2.dp,
    val md: androidx.compose.ui.unit.Dp = 4.dp,
    val lg: androidx.compose.ui.unit.Dp = 8.dp,
    val xl: androidx.compose.ui.unit.Dp = 12.dp
)

val AppElevation = Elevation()

data class CardDefaults(
    val defaultPadding: androidx.compose.ui.unit.Dp = AppSpacing.lg,
    val contentSpacing: androidx.compose.ui.unit.Dp = AppSpacing.md,
    val buttonSpacing: androidx.compose.ui.unit.Dp = AppSpacing.sm,
    val elevation: androidx.compose.ui.unit.Dp = AppElevation.none,
    val pinnedElevation: androidx.compose.ui.unit.Dp = AppElevation.xs
)

val AppCardDefaults = CardDefaults()

// Composable helpers for common sizes that might be used across the app
@Composable
fun iconSmall() = 18.dp

@Composable
fun iconMedium() = 24.dp

@Composable
fun iconLarge() = 32.dp

@Composable
fun iconExtraLarge() = 48.dp

@Composable
fun iconButtonSize() = 40.dp
