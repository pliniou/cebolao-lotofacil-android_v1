package com.cebolao.lotofacil.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    val xs: androidx.compose.ui.unit.Dp = 0.5.dp,
    val sm: androidx.compose.ui.unit.Dp = 1.dp,
    val md: androidx.compose.ui.unit.Dp = 2.dp,
    val lg: androidx.compose.ui.unit.Dp = 4.dp,
    val xl: androidx.compose.ui.unit.Dp = 8.dp
)

val AppElevation = Elevation()

data class CardDefaults(
    val defaultPadding: androidx.compose.ui.unit.Dp = AppSpacing.lg,
    val contentSpacing: androidx.compose.ui.unit.Dp = AppSpacing.md,
    val buttonSpacing: androidx.compose.ui.unit.Dp = AppSpacing.sm,
    val elevation: androidx.compose.ui.unit.Dp = AppElevation.none,
    val pinnedElevation: androidx.compose.ui.unit.Dp = AppElevation.xs,
    val hoverElevation: androidx.compose.ui.unit.Dp = AppElevation.sm
)

val AppCardDefaults = CardDefaults(
    elevation = AppElevation.none,
    hoverElevation = AppElevation.sm
)

object Animations {
    // Spring animations for interactive elements
    val buttonPress = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val cardHover = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val elevationChange = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    // Tween animations for state transitions
    val colorTransition = tween<Color>(
        durationMillis = 300
    )
    
    val alphaFade = tween<Float>(
        durationMillis = 200,
        easing = LinearOutSlowInEasing
    )
    
    val slideIn = tween<Float>(
        durationMillis = 250,
        easing = LinearEasing
    )
    
    val scaleBounce = tween<Float>(
        durationMillis = 150,
        easing = FastOutSlowInEasing
    )
}

object AppShapes {
    // Corner radii for different components
    val none = androidx.compose.foundation.shape.ZeroCornerSize
    
    val xs = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    val sm = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
    val md = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    val lg = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val xl = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val xxl = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    val xxxl = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    
    // Specialized shapes
    val circle = androidx.compose.foundation.shape.CircleShape
    val button = sm
    val card = md
    val dialog = xl
    val listItem = xs
    val chip = sm
    val badge = xs
    val numberBall = androidx.compose.foundation.shape.CircleShape
}

// Composable helpers for common sizes that might be used across the app
@Composable
fun iconSmall() = 24.dp

@Composable
fun iconMedium() = 24.dp

@Composable
fun iconLarge() = 32.dp

@Composable
fun iconExtraLarge() = 48.dp

@Composable
fun iconButtonSize() = 48.dp
