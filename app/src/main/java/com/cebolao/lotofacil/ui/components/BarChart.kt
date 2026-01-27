package com.cebolao.lotofacil.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun BarChart(
    data: ImmutableList<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 200.dp,
    maxValue: Int,
    showGaussCurve: Boolean = true,
    highlightThreshold: Int? = null
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val density = LocalDensity.current
    val chartScaleFactor = density.density.coerceIn(0.85f, 1.3f)
    val baseTextSize = 10.sp * chartScaleFactor

    val textPaint = remember(density, onSurfaceVariant, baseTextSize) {
        Paint().apply {
            isAntiAlias = true
            textSize = density.run { baseTextSize.toPx() }
            color = onSurfaceVariant.toArgb()
            textAlign = Paint.Align.RIGHT
        }
    }
    val valuePaint = remember(density, primaryColor, baseTextSize) {
        Paint().apply {
            isAntiAlias = true
            textSize = density.run { baseTextSize.toPx() }
            color = primaryColor.toArgb()
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
    }
    val labelPaint = remember(density, onSurfaceVariant, baseTextSize) {
        Paint().apply {
            isAntiAlias = true
            textSize = density.run { baseTextSize.toPx() }
            color = onSurfaceVariant.toArgb()
            textAlign = Paint.Align.CENTER
        }
    }

    Canvas(modifier = modifier.height(chartHeight)) {
        val yAxisLabelWidth = 36.dp.toPx()
        val xAxisLabelHeight = 36.dp.toPx()
        val valueLabelHeight = 22.dp.toPx()
        val chartAreaWidth = size.width - yAxisLabelWidth
        val chartAreaHeight = size.height - xAxisLabelHeight - valueLabelHeight

        // Draw refined grid
        drawGrid(
            yAxisLabelWidth,
            chartAreaHeight,
            valueLabelHeight,
            maxValue,
            textPaint,
            outlineVariant
        )

        val barSpacing = 10.dp.toPx()
        val totalSpacing = barSpacing * (data.size + 1)
        val barWidth = (chartAreaWidth - totalSpacing).coerceAtLeast(0f) / data.size

        // Draw Gaussian Curve Backdrop
        if (showGaussCurve && data.size > 2) {
            drawGaussianCurve(
                data = data,
                yAxisLabelWidth = yAxisLabelWidth,
                chartAreaHeight = chartAreaHeight,
                topPadding = valueLabelHeight,
                maxValue = maxValue,
                color = tertiaryColor.copy(alpha = 0.3f)
            )
        }

        data.forEachIndexed { index, (label, value) ->
            val progressFactor = animatedProgress.value
            val barHeight = (value.toFloat() / maxValue) * chartAreaHeight * progressFactor
            val left = yAxisLabelWidth + barSpacing + index * (barWidth + barSpacing)
            val barCenterX = left + barWidth / 2

            // Bar background (Subtle surface)
            drawRoundRect(
                color = surfaceVariant.copy(alpha = 0.05f),
                topLeft = Offset(left, valueLabelHeight),
                size = Size(barWidth, chartAreaHeight),
                cornerRadius = CornerRadius(barWidth / 5)
            )

            // Actual data bar with Gradient and Glow effect
            if (barHeight > 0) {
                val isHighlighted = highlightThreshold != null && value >= highlightThreshold
                val barColor1 = if (isHighlighted) tertiaryColor else primaryColor
                val barColor2 = if (isHighlighted) tertiaryContainer else secondaryColor

                // Subtle glow/shadow
                // Glow - removed for flat design
                /*
                drawRoundRect(
                    color = barColor1.copy(alpha = if (isHighlighted) 0.3f else 0.15f),
                    topLeft = Offset(left - 2.dp.toPx(), valueLabelHeight + chartAreaHeight - barHeight - 2.dp.toPx()),
                    size = Size(barWidth + 4.dp.toPx(), barHeight + 4.dp.toPx()),
                    cornerRadius = CornerRadius(x = barWidth / 2.5f, y = barWidth / 2.5f)
                )
                */

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColor1, barColor2),
                        startY = valueLabelHeight + chartAreaHeight - barHeight,
                        endY = valueLabelHeight + chartAreaHeight
                    ),
                    topLeft = Offset(left, valueLabelHeight + chartAreaHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(x = barWidth / 3, y = barWidth / 3)
                )
            }

            // Value text above the bar with scale animation
            val valueTextY = valueLabelHeight + chartAreaHeight - barHeight - 8.dp.toPx()
            if (progressFactor > 0.6f) {
                val textAlpha = ((progressFactor - 0.6f) * 2.5f).coerceIn(0f, 1f)
                valuePaint.alpha = (textAlpha * 255).toInt()
                drawContext.canvas.nativeCanvas.drawText(
                    value.toString(),
                    barCenterX,
                    valueTextY,
                    valuePaint
                )
            }

            // X-Axis Label
            val labelTextY = size.height - xAxisLabelHeight + 16.dp.toPx()
            drawContext.canvas.nativeCanvas.save()
            drawContext.canvas.nativeCanvas.rotate(45f, barCenterX, labelTextY)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                barCenterX,
                labelTextY,
                labelPaint
            )
            drawContext.canvas.nativeCanvas.restore()
        }
    }
}

private fun DrawScope.drawGaussianCurve(
    data: ImmutableList<Pair<String, Int>>,
    yAxisLabelWidth: Float,
    chartAreaHeight: Float,
    topPadding: Float,
    maxValue: Int,
    color: Color
) {
    val barSpacing = 10.dp.toPx()
    val chartAreaWidth = size.width - yAxisLabelWidth
    val barWidthWithSpacing = chartAreaWidth / data.size
    
    // Calculate mean and standard deviation of categories (indices)
    // We assume the distribution follows the bars' values
    val totalWeight = data.sumOf { it.second }.toFloat().coerceAtLeast(1f)
    var mean = 0f
    data.forEachIndexed { index, pair ->
        mean += index * (pair.second / totalWeight)
    }
    
    var variance = 0f
    data.forEachIndexed { index, pair ->
        variance += (index - mean).pow(2) * (pair.second / totalWeight)
    }
    val stdDev = sqrt(variance).coerceAtLeast(0.5f)

    val path = Path()
    val points = 50
    for (i in 0..points) {
        val xRelative = i.toFloat() / points * (data.size - 1)
        val x = yAxisLabelWidth + barSpacing + (xRelative * barWidthWithSpacing) + (barWidthWithSpacing - barSpacing) / 2
        
        // Normal distribution formula
        val exponent = -0.5f * ((xRelative - mean) / stdDev).pow(2)
        val gaussianValue = exp(exponent)
        
        // Scale to fit chart height (using max observed value as peak reference)
        val y = topPadding + chartAreaHeight - (gaussianValue * chartAreaHeight * (data.maxOf { it.second }.toFloat() / maxValue))
        
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
    )
}

private fun DrawScope.drawGrid(
    yAxisLabelWidth: Float,
    chartAreaHeight: Float,
    topPadding: Float,
    maxValue: Int,
    textPaint: Paint,
    lineColor: Color
) {
    val gridLines = 4
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))

    (0..gridLines).forEach { i ->
        val y = topPadding + chartAreaHeight * (1f - i.toFloat() / gridLines)
        val value = (maxValue * i.toFloat() / gridLines).roundToInt()
        
        // Horizontal grid line
        drawLine(
            color = lineColor.copy(alpha = 0.2f),
            start = Offset(yAxisLabelWidth, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = if (i == 0) null else dashEffect
        )
        
        // Y-Axis labels
        val textY = y + (textPaint.descent() - textPaint.ascent()) / 2 - textPaint.descent()
        drawContext.canvas.nativeCanvas.drawText(
            value.toString(),
            yAxisLabelWidth - 6.dp.toPx(),
            textY,
            textPaint
        )
    }
    
    // Y-Axis line
    drawLine(
        color = lineColor.copy(alpha = 0.3f),
        start = Offset(yAxisLabelWidth, topPadding),
        end = Offset(yAxisLabelWidth, topPadding + chartAreaHeight),
        strokeWidth = 1.5.dp.toPx()
    )
}
