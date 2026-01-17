package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
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
                                contentDescription = stringResource(
                                    id = if (showPrizes) R.string.prize_details_collapse else R.string.prize_details_expand
                                ),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(id = R.string.prize_details_title),
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
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                            ) {
                                // Enhanced prize tiers section
                                for ((index, prize) in stats.prizes.withIndex()) {
                                    EnhancedPrizeTierRow(prize, index + 1, currencyFormat)
                                }
                               
                                if (stats.winners.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(AppSpacing.md))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                                    
                                    Text(
                                        text = stringResource(id = R.string.winners_by_state),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = AppSpacing.sm)
                                    )
                                    
                                    WinnersByStateGrid(winners = stats.winners)
                                }
                            }
                        }
                    }
                }
            }
        )

        // Card do Próximo Concurso
        stats.nextContest?.let { 
            NextDrawCard(
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
private fun EnhancedPrizeTierRow(prize: PrizeTier, tier: Int, format: NumberFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = prize.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(id = R.string.prize_tier_format, tier),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${prize.winners} ganhador(es)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = format.format(prize.prizeValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun WinnersByStateGrid(winners: List<WinnerLocation>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        contentPadding = PaddingValues(horizontal = AppSpacing.md)
    ) {
        items(winners.take(5)) { winner ->
            Card(
                modifier = Modifier.size(width = 80.dp, height = 40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppSpacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = winner.state,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "${winner.winnersCount}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun NextDrawCard(
    contest: Int,
    date: String,
    estimate: Double,
    accumulated: Boolean,
    currencyFormat: NumberFormat
) {
    StatCard(
        title = stringResource(id = R.string.next_contest_format, contest),
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
