package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.domain.model.PrizeTier
import com.cebolao.lotofacil.domain.model.WinnerLocation
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.components.NumberBallVariant
import com.cebolao.lotofacil.ui.components.cards.StatCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LastDrawSection(stats: LastDrawStats) {
    var showPrizes by remember { mutableStateOf(false) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")) }

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
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
                        for (number in sortedNumbers) {
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

                    if (stats.prizes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(AppSpacing.lg))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrizes = !showPrizes }
                                .padding(vertical = AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (showPrizes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (showPrizes) stringResource(id = R.string.hide_prizes) else stringResource(id = R.string.show_prizes),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        AnimatedVisibility(
                            visible = showPrizes,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.sm),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                            ) {
                                for (prize in stats.prizes) {
                                    PrizeRow(prize, currencyFormat)
                                }
                                
                                if (stats.winners.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(AppSpacing.md))
                                    Text(
                                        text = stringResource(id = R.string.main_winners_label),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = AppSpacing.xs)
                                    )
                                    for (winner in stats.winners) {
                                        WinnerRow(winner)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )

        // Card do Próximo Concurso
        stats.nextContest?.let { 
            NextContestCard(
                contest = it,
                date = stats.nextDate ?: "",
                estimate = stats.nextEstimate ?: 0.0,
                accumulated = stats.accumulated,
                currencyFormat = currencyFormat
            )
        }
    }
}

@Composable
private fun PrizeRow(prize: PrizeTier, format: NumberFormat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Icon(
                Icons.Default.EmojiEvents, 
                contentDescription = null, 
                modifier = Modifier.size(14.dp), 
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
            )
            Text(prize.description, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            "${prize.winners} ganhadores • ${format.format(prize.prizeValue)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun WinnerRow(winner: WinnerLocation) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocationOn, 
            contentDescription = null, 
            modifier = Modifier.size(14.dp), 
            tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
        )
        Text(
            "${winner.winnersCount} ganhador(es) em ${winner.city}/${winner.state}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NextContestCard(
    contest: Int,
    date: String,
    estimate: Double,
    accumulated: Boolean,
    currencyFormat: NumberFormat
) {
    StatCard(
        title = "Próximo Concurso: #$contest",
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(AppSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = if (accumulated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (accumulated) stringResource(id = R.string.accumulated) else stringResource(id = R.string.prize_estimate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (accumulated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = currencyFormat.format(estimate),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = stringResource(id = R.string.next_draw_date, date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
