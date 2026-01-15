package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing

/**
 * Reusable skeleton components for loading states.
 * Provides consistent placeholder UI across the app.
 */
object SkeletonDefaults {
    val Color: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceVariant
    val Height = 24.dp
    val CornerRadius = 8.dp
    val Spacing = AppSpacing.sm
}

@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = SkeletonDefaults.Height
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(SkeletonDefaults.CornerRadius))
            .background(SkeletonDefaults.Color)
    )
}

@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = 120.dp,
    height: androidx.compose.ui.unit.Dp = 16.dp
) {
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(SkeletonDefaults.CornerRadius))
            .background(SkeletonDefaults.Color)
    )
}

@Composable
fun SkeletonNumberBall(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(SkeletonDefaults.CornerRadius))
            .background(SkeletonDefaults.Color)
    )
}

@Composable
fun SkeletonRow(
    modifier: Modifier = Modifier,
    itemCount: Int = 3,
    itemHeight: androidx.compose.ui.unit.Dp = 24.dp,
    itemWidth: androidx.compose.ui.unit.Dp = 80.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SkeletonDefaults.Spacing)
    ) {
        repeat(itemCount) {
            SkeletonCard(
                modifier = Modifier.weight(1f),
                height = itemHeight
            )
        }
    }
}

@Composable
fun SkeletonStatsRow(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SkeletonDefaults.Spacing)
    ) {
        repeat(itemCount) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SkeletonText(
                    width = 60.dp,
                    height = 20.dp
                )
                Spacer(modifier = Modifier.height(SkeletonDefaults.Spacing))
                SkeletonText(
                    width = 40.dp,
                    height = 16.dp
                )
            }
        }
    }
}
