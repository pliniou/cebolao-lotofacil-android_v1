package com.cebolao.lotofacil.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Centralized animation constants and specs
 * Ensures consistency across all Compose animations
 * 
 * All duration values in milliseconds
 */
object AppAnimationConstants {
    
    // Duration timings (milliseconds)
    object Durations {
        // Very fast micro-interactions
        const val VeryShort = 100
        
        // Button feedback, state changes
        const val Short = 200
        
        // List item animations, card transitions
        const val Medium = 300
        
        // Screen transitions, complex sequences
        const val Long = 500
        
        // Elaborate sequences, delayed animations
        const val VeryLong = 1000
        
        // Number ball state changes (specific to lottery app)
        const val NumberBall = Medium
        
        // Filter panel animations
        const val Filter = Short
        
        // Content visibility toggles
        const val Visibility = Medium
        
        // SnackBar display
        const val SnackBar = Short
    }
    
    // Easing functions
    object Easings {
        // Standard Material curve (↦ acceleration then deceleration)
        val Standard = FastOutSlowInEasing
        
        // Linear (constant speed)
        val Linear = LinearEasing
        
        // Decelerate (fast start, slow end)
        val Decelerate = LinearOutSlowInEasing
        
        // Custom cubic bezier (for specific needs)
        val Gentle = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
        val Quick = CubicBezierEasing(0.2f, 0f, 0.8f, 0.15f)
    }
    
    // Pre-configured animation specs
    object Specs {
        // Standard tween (duration + easing)
        fun standard(durationMillis: Int = Durations.Medium) = 
            tween(durationMillis, easing = Easings.Standard)
        
        // Quick response (shorter, linear)
        fun quick() = 
            tween(Durations.Short, easing = Easings.Quick)
        
        // Smooth spring (physics-based)
        fun spring(
            dampingRatio: Float = Spring.DampingRatioMediumBouncy,
            stiffness: Float = Spring.StiffnessMediumLow
        ) = spring(dampingRatio = dampingRatio, stiffness = stiffness)
        
        // Gentle deceleration
        fun gentle(durationMillis: Int = Durations.Medium) =
            tween(durationMillis, easing = Easings.Gentle)
    }
    
    // Delay constants for staggered animations
    object Delays {
        const val None = 0
        const val Minimal = 50
        const val Short = 100
        const val Medium = 150
        const val Long = 200
    }
}
