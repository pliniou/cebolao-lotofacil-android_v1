package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.cards.StatCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconMedium

@Composable
fun StatisticsExplanationCard() {
    StatCard(
        title = stringResource(id = R.string.stats_guide_title),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                modifier = Modifier.padding(AppSpacing.md)
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
                        text = stringResource(id = R.string.how_filters_work),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                
                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_soma_dezenas_title),
                    description = "A soma total dos 15 números sorteados. Na Lotofácil, a maioria dos sorteios fica na faixa de 170 a 220."
                )
                
                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_pares_title),
                   description = "O equilíbrio entre números pares e ímpares. O padrão mais comum é 7 pares e 8 ímpares (ou vice-versa)."
                )

                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_primos_title),
                    description = "Números que só são divisíveis por 1 e por eles mesmos (ex: 2, 3, 5, 7, 11, 13, 17, 19, 23)."
                )

                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_moldura_title),
                    description = "A Moldura são os números das bordas do volante (16 números). O Retrato são os 9 números centrais."
                )

                FilterExplanationItem(
                    title = "Frequência e Atraso",
                    description = "A Frequência mostra quais números saem mais. O Atraso indica há quantos concursos um número não é sorteado."
                )
            }
        }
    )
}

@Composable
private fun FilterExplanationItem(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
