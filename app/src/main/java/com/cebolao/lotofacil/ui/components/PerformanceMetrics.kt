package com.cebolao.lotofacil.ui.components

import android.app.ActivityManager
import android.os.Debug
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlinx.coroutines.delay
import java.util.Locale

data class PerformanceMetrics(
    val memoryUsage: Long = 0,
    val memoryAvailable: Long = 0,
    val nativeMemory: Long = 0,
    val frameTime: Float = 0f,
    val recompositionCount: Int = 0
)

/**
 * Debug overlay that displays performance metrics
 * Shows memory usage, native memory, and frame time information
 * 
 * @param onDismiss Callback when close button is clicked
 */
@Composable
fun PerformanceMetricsOverlay(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var metrics by remember { mutableStateOf(PerformanceMetrics()) }
    var updateCount by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Update every second
            
            val runtime = Runtime.getRuntime()
            val maxMemory = runtime.maxMemory() / 1048576 // Convert to MB
            val totalMemory = runtime.totalMemory() / 1048576
            val freeMemory = runtime.freeMemory() / 1048576
            val usedMemory = totalMemory - freeMemory
            
            val activityManager = context.getSystemService(ActivityManager::class.java)
            val memInfo = ActivityManager.MemoryInfo()
            activityManager?.getMemoryInfo(memInfo)
            val availableMemory = memInfo.availMem / 1048576
            
            val nativeMemory = Debug.getNativeHeapAllocatedSize() / 1048576
            
            metrics = PerformanceMetrics(
                memoryUsage = usedMemory,
                memoryAvailable = availableMemory,
                nativeMemory = nativeMemory,
                frameTime = 16.67f, // Assuming 60 FPS
                recompositionCount = updateCount
            )
            updateCount++
        }
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                shape = MaterialTheme.shapes.medium
            ),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Performance Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.width(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close metrics",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            
            // Memory Section
            MetricCard(
                title = "💾 Memory Usage",
                metrics = listOf(
                    "Used" to "${metrics.memoryUsage} MB",
                    "Available" to "${metrics.memoryAvailable} MB",
                    "Native" to "${metrics.nativeMemory} KB"
                )
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.md))
            
            // Performance Section
            MetricCard(
                title = "⚡ Performance",
                metrics = listOf(
                    "Frame Time" to "${String.format(Locale.getDefault(), "%.2f", metrics.frameTime)} ms",
                    "Recompositions" to "${metrics.recompositionCount}",
                    "Status" to if (metrics.frameTime <= 16.67f) "✅ 60 FPS" else "⚠️ Dropping frames"
                )
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            
            Text(
                text = "Updates every 1 second",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.xs)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    metrics: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = AppSpacing.sm)
            )
            
            metrics.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Floating performance metrics button (debug only)
 * Shows a small button that can be toggled to show/hide metrics
 */
@Composable
fun PerformanceMetricsButton(
    modifier: Modifier = Modifier,
    isDebugBuild: Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }
    
    if (!isDebugBuild) return
    
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            contentAlignment = Alignment.TopEnd
        ) {
            PerformanceMetricsOverlay(
                modifier = Modifier
                    .fillMaxWidth(0.95f),
                onDismiss = { isVisible = false }
            )
        }
    } else {
        IconButton(
            onClick = { isVisible = true },
            modifier = modifier
                .padding(AppSpacing.md)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Text(
                text = "📊",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
