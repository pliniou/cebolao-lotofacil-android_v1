package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.cards.StatCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconMedium

@Composable
fun StatisticsExplanationCard() {
    StatCard(
        title = stringResource(id = R.string.understanding_statistics),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(iconMedium())
                    )
                    Text(
                        text = stringResource(id = R.string.how_to_read_data),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.statistics_explanation_delayed_prefix))
                        }
                        append(stringResource(id = R.string.statistics_explanation_delayed_body))
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.statistics_explanation_distribution_prefix))
                        }
                        append(stringResource(id = R.string.statistics_explanation_distribution_body))
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
