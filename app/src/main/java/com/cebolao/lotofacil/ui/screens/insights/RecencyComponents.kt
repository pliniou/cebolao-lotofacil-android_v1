package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun RecencySection(
    overdueNumbers: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier
) {
    val sortedOverdue = remember(overdueNumbers) {
        overdueNumbers.sortedByDescending { it.second }
    }

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = stringResource(id = R.string.recency_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.recency_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                items(sortedOverdue, key = { it.first }) { (number, count) ->
                    OverdueItem(number = number, count = count)
                }
            }
        }
    }
}

@Composable
private fun OverdueItem(number: Int, count: Int) {
    val (color, label) = remember(count) {
        when {
            count <= 1 -> Pair(Color(0xFF4CAF50), "0-1")
            count <= 3 -> Pair(Color(0xFFFFC107), "2-3")
            count <= 6 -> Pair(Color(0xFFFF9800), "4-6")
            else -> Pair(Color(0xFFF44336), "+7")
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        NumberBall(
            number = number,
            size = 48.dp
        )
        
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(24.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        Text(
            text = stringResource(id = R.string.drawn_ago),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
