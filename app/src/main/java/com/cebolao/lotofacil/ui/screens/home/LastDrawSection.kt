package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.ui.components.NumberBallVariant
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.components.cards.StatCard
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun LastDrawSection(stats: LastDrawStats) {
    StatCard(
        title = "${stringResource(id = R.string.last_contest)}: #${stats.contest}",
        content = {
            val sortedNumbers = remember(stats.numbers) { stats.numbers.sorted() }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm, Alignment.CenterHorizontally),
                    maxItemsInEachRow = 5
                ) {
                    sortedNumbers.forEach { number ->
                        NumberBall(
                        number = number,
                        variant = NumberBallVariant.Lotofacil
                    )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(AppSpacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = stringResource(id = R.string.sum_label), value = stats.sum.toString())
                    StatItem(label = stringResource(id = R.string.even_label), value = stats.evens.toString())
                    StatItem(label = stringResource(id = R.string.prime_label), value = stats.primes.toString())
                    StatItem(label = stringResource(id = R.string.frame_label), value = stats.frame.toString())
                    StatItem(label = stringResource(id = R.string.portrait_label), value = stats.portrait.toString())
                }
            }
        }
    )
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
