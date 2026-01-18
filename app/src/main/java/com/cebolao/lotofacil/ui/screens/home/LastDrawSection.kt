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
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.LocalAppColors
import com.cebolao.lotofacil.ui.theme.iconMedium
import com.cebolao.lotofacil.ui.theme.iconSmall
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LastDrawSection(stats: LastDrawStats) {
    val colors = LocalAppColors.current
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
                    HorizontalDivider(color = colors.outline.copy(alpha = 0.5f))
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
                        HorizontalDivider(color = colors.outline.copy(alpha = 0.3f))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrizes = !showPrizes }
                                .padding(vertical = AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (showPrizes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = stringResource(
                                    id = if (showPrizes) R.string.prize_details_collapse else R.string.prize_details_expand
                                ),
                                tint = colors.brandPrimary,
                                modifier = Modifier.size(iconSmall())
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.xs))
                            Text(
                                text = stringResource(id = R.string.prize_details_title),
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.brandPrimary,
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
                                    HorizontalDivider(color = colors.outline.copy(alpha = 0.2f))
                                    
                                    Text(
                                        text = stringResource(id = R.string.winners_by_state),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = colors.textPrimary,
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
    val colors = LocalAppColors.current
    
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
                modifier = Modifier.size(iconSmall()), 
                tint = colors.brandSecondary
            )
            Text(
                text = prize.description, 
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
        Text(
            "${prize.winners} ganhadores • ${format.format(prize.prizeValue)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun WinnerRow(winner: WinnerLocation) {
    val colors = LocalAppColors.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocationOn, 
            contentDescription = null, 
            modifier = Modifier.size(iconSmall()), 
            tint = colors.textTertiary
        )
        Text(
            "${winner.winnersCount} ganhador(es) em ${winner.city}/${winner.state}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun EnhancedPrizeTierRow(prize: PrizeTier, tier: Int, format: NumberFormat) {
    val colors = LocalAppColors.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface2
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.xs)
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
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(id = R.string.prize_tier_format, tier),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${prize.winners} ganhador(es)",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.brandPrimary
                )
                Text(
                    text = format.format(prize.prizeValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.brandPrimary
                )
            }
        }
    }
}

@Composable
private fun WinnersByStateGrid(winners: List<WinnerLocation>) {
    val colors = LocalAppColors.current
    
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        contentPadding = PaddingValues(horizontal = AppSpacing.md)
    ) {
        items(winners.take(5)) { winner ->
            Card(
                modifier = Modifier.size(width = 80.dp, height = 40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface3
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.none)
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
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${winner.winnersCount}",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.brandPrimary
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
    val colors = LocalAppColors.current
    
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
                        modifier = Modifier.size(iconMedium()),
                        tint = if (accumulated) colors.error else colors.brandPrimary
                    )
                    Text(
                        text = if (accumulated) stringResource(id = R.string.accumulated) else stringResource(id = R.string.prize_estimate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (accumulated) colors.error else colors.brandPrimary
                    )
                }
                
                Text(
                    text = currencyFormat.format(estimate),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary
                )
                
                Text(
                    text = stringResource(id = R.string.next_draw_date, date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    )
}

@Composable
private fun StatItem(label: String, value: String) {
    val colors = LocalAppColors.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.brandPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
    }
}
