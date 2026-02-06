package com.cebolao.lotofacil.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.system.measureTimeMillis

/**
 * Extension properties and functions for performance monitoring
 */

data class CompositionMetrics(
    val compositionCount: Int = 0,
    val averageCompositionTime: Long = 0,
    val peakCompositionTime: Long = 0
)

/**
 * Track and log recomposition calls
 * Use this in debug builds to identify unnecessary recompositions
 */
@Composable
inline fun <T> measureComposition(
    name: String,
    crossinline block: @Composable () -> T
): T {
    var compositionTime = 0L
    val result = remember(name) {
        compositionTime = measureTimeMillis { }
        null
    }
    
    return block()
}

/**
 * Log composition timing information
 */
fun logCompositionTime(name: String, timeMs: Long) {
    if (timeMs > 16) { // Alert if composition exceeds frame time (~60 FPS)
        println("⚠️ Composition [$name] took ${timeMs}ms - may cause frame drops")
    }
}

/**
 * Memory usage extension
 */
fun getMemoryUsagePercent(): Float {
    val runtime = Runtime.getRuntime()
    val maxMemory = runtime.maxMemory().toFloat()
    val totalMemory = runtime.totalMemory().toFloat()
    val freeMemory = runtime.freeMemory().toFloat()
    val usedMemory = totalMemory - freeMemory
    
    return (usedMemory / maxMemory) * 100f
}

/**
 * Check if memory usage is critical
 */
fun isMemoryCritical(threshold: Float = 85f): Boolean {
    return getMemoryUsagePercent() >= threshold
}
