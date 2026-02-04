package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun TrendChart(
    data: List<Pair<Int, Float>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    chartHeight: Dp = 200.dp
) {
    if (data.isEmpty()) return

    val colors = MaterialTheme.colorScheme
    val maxVal = remember(data) { data.maxOf { it.second }.coerceAtLeast(1f) }
    val minVal = remember(data) { data.minOf { it.second }.coerceAtMost(maxVal - 0.1f) }
    val range = maxVal - minVal

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(chartHeight)
            .padding(horizontal = AppSpacing.sm)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val stepX = width / (data.size - 1).coerceAtLeast(1)

            val points = data.mapIndexed { index, pair ->
                val x = index * stepX
                val y = height - ((pair.second - minVal) / range) * height
                Offset(x, y)
            }

            // Draw line
            val path = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Draw points (dots)
            points.forEach { offset ->
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = offset
                )
            }
        }
    }
}
