package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.data.LotofacilConstants
import com.cebolao.lotofacil.viewmodels.GenerationUiState
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GenerationActionsPanel(
    generationState: GenerationUiState,
    onGenerate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val options = remember { listOf(1, 2, 3, 5, 7, 9, 10, 12, 15, 20) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val quantity = options[selectedIndex]
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val isLoading = generationState is GenerationUiState.Loading

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quantidade de Jogos", style = MaterialTheme.typography.titleMedium)
                QuantitySelector(
                    quantity = quantity,
                    onDecrement = {
                        if (selectedIndex > 0) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex--
                        }
                    },
                    onIncrement = {
                        if (selectedIndex < options.lastIndex) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex++
                        }
                    },
                    isDecrementEnabled = selectedIndex > 0 && !isLoading,
                    isIncrementEnabled = selectedIndex < options.lastIndex && !isLoading
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Custo Total:", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = currencyFormat.format(
                        LotofacilConstants.GAME_COST.multiply(BigDecimal(quantity))
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onGenerate(quantity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.large
            ) {
                AnimatedContent(
                    targetState = isLoading,
                    label = "GenerateButtonContent"
                ) { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Gerar $quantity ${if (quantity > 1) "Jogos" else "Jogo"}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    isDecrementEnabled: Boolean,
    isIncrementEnabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onDecrement, enabled = isDecrementEnabled) {
            Icon(Icons.Filled.Remove, "Diminuir")
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.widthIn(min = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        IconButton(onClick = onIncrement, enabled = isIncrementEnabled) {
            Icon(Icons.Filled.Add, "Aumentar")
        }
    }
}