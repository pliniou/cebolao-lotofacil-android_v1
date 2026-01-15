package com.cebolao.lotofacil.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R

@Composable
fun FilterRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @StringRes minLabelResId: Int = R.string.min_value,
    @StringRes maxLabelResId: Int = R.string.max_value
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ValueIndicator(stringResource(id = minLabelResId), value.start.toInt())
            ValueIndicator(stringResource(id = maxLabelResId), value.endInclusive.toInt(), Alignment.End)
        }
        
        RangeSlider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled
        )
    }
}

@Composable
private fun ValueIndicator(
    label: String, 
    value: Int, 
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(horizontalAlignment = alignment) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
