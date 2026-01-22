package com.cebolao.lotofacil.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
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
) {
    companion object {
        fun fromColorScheme(
            colorScheme: ColorScheme,
            surface3Alpha: Float = 0.94f
        ): AppColors {
            val safeAlpha = surface3Alpha.coerceIn(0f, 1f)
            val surface3Color = colorScheme.surface.copy(alpha = safeAlpha)
            return AppColors(
                background = colorScheme.background,
                surface1 = colorScheme.surface,
                surface2 = colorScheme.surfaceVariant,
                surface3 = surface3Color,
                outline = colorScheme.outline,
                textPrimary = colorScheme.onBackground,
                textSecondary = colorScheme.onSurfaceVariant,
                textTertiary = colorScheme.outlineVariant,
                brandPrimary = colorScheme.primary,
                brandSecondary = colorScheme.secondary,
                brandSubtle = colorScheme.primaryContainer,
                success = Success,
                warning = Warning,
                error = colorScheme.error,
                disabledContainer = colorScheme.surfaceVariant.copy(alpha = 0.84f),
                disabledContent = colorScheme.onSurfaceVariant.copy(alpha = 0.68f)
            )
        }
    }
}

private val DefaultAppColors = AppColors(
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

val LocalAppColors = staticCompositionLocalOf { DefaultAppColors }

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

val AppCardDefaults = CardDefaults(
    elevation = AppElevation.none
)

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
