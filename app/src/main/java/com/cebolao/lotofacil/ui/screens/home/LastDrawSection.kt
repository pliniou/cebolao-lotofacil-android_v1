package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.ui.components.NumberBallVariant
import com.cebolao.lotofacil.ui.components.OptimizedNumberBall
import com.cebolao.lotofacil.ui.components.cards.StatCard
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun LastDrawSection(stats: LastDrawStats) {
    StatCard(
        title = "${stringResource(id = R.string.last_contest)}: #${stats.contest}",
        content = {
            val sortedNumbers = remember(stats.numbers) { stats.numbers.sorted() }
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm, Alignment.CenterHorizontally),
                maxItemsInEachRow = 5
            ) {
                sortedNumbers.forEach { number ->
                    OptimizedNumberBall(
                        number = number,
                        variant = NumberBallVariant.Primary
                    )
                }
            }
        }
    )
}
