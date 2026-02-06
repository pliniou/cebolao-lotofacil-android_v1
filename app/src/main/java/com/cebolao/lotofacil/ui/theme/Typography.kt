@file:Suppress("SameParameterValue")
package com.cebolao.lotofacil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R

val Inter = FontFamily(
    Font(R.font.gabarito_regular, FontWeight.Normal),
    Font(R.font.gabarito_medium, FontWeight.Medium),
    Font(R.font.gabarito_semibold, FontWeight.SemiBold),
    Font(R.font.gabarito_bold, FontWeight.Bold)
)

/**
 * Calculates an adaptive scale factor based on screen configuration and accessibility settings.
 * This ensures text remains readable and properly sized across different devices
 * (phones, tablets, foldables) while respecting user accessibility settings for font size.
 *
 * Font size scaling from Android Settings > Accessibility > Display > Font Size:
 * - Small: 0.85f
 * - Normal: 1.0f (default)
 * - Large: 1.15f
 * - Larger: 1.3f
 * - Largest: 1.5f
 *
 * @return Scale factor in range [0.85f, 1.5f]
 */
@Composable
fun calculateTypographyScaleFactor(): Float {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    // Base calculation on smallest screen width
    val smallestScreenWidthDp = configuration.smallestScreenWidthDp
    
    // Calculate base scale factor
    // - Small phones (<360dp): slightly smaller text (0.9f)
    // - Standard phones (360-480dp): baseline (1.0f)
    // - Large phones/small tablets (480-600dp): slightly larger (1.05f)
    // - Tablets (>600dp): larger text (1.1f)
    val baseScaleFactor = when {
        smallestScreenWidthDp < 360 -> 0.90f
        smallestScreenWidthDp < 480 -> 1.00f
        smallestScreenWidthDp < 600 -> 1.05f
        else -> 1.10f
    }
    
    // Factor in density for very high or very low density screens
    val densityAdjustment = when {
        density.density < 1.5f -> 0.95f  // Low density screens
        density.density > 3.0f -> 1.05f  // Very high density screens
        else -> 1.0f
    }
    
    // Accessibility font size scaling (fontScale from Configuration)
    // fontScale ranges from 0.85 to 1.5 based on user settings
    val accessibilityScaling = configuration.fontScale.coerceIn(0.85f, 1.5f)
    
    // Combine factors and constrain to safe range
    val finalScale = (baseScaleFactor * densityAdjustment * accessibilityScaling).coerceIn(0.75f, 1.75f)
    
    return finalScale
}

/**
 * Creates Material3 Typography with adaptive scaling.
 * All text styles will scale based on device configuration.
 * Optimized for performance with pre-calculated font sizes.
 *
 * @param scaleFactor Multiplier for all font sizes (default calculated from device config)
 */
@Composable
fun createAdaptiveTypography(scaleFactor: Float = calculateTypographyScaleFactor()): Typography {
    return Typography(
        headlineLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (28 * scaleFactor).sp,
            lineHeight = (36 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (24 * scaleFactor).sp,
            lineHeight = (32 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * scaleFactor).sp,
            lineHeight = (28 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleSmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (18 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        bodySmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        labelMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.sp
        )
    )
}
