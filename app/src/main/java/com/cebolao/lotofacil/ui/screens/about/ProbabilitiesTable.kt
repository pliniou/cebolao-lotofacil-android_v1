package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProbabilitiesTable(modifier: Modifier = Modifier) {
    val data = remember {
        listOf(
            "15 acertos" to "1 em 3.268.760",
            "14 acertos" to "1 em 21.792",
            "13 acertos" to "1 em 692",
            "12 acertos" to "1 em 60",
            "11 acertos" to "1 em 11"
        )
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TableHeader()
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        data.forEachIndexed { idx, (prize, prob) ->
            TableRow(prize, prob, isHighlighted = idx == 0)
            if (idx < data.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Prêmio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Probabilidade", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TableRow(prize: String, probability: String, isHighlighted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            prize,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
        Text(
            probability,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
