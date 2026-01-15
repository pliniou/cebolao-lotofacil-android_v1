package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R

@Composable
fun BottomActionsBar(
    selectedCount: Int,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    onClearClick: () -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 52.dp,
    shape: Shape = MaterialTheme.shapes.small
) {
    Surface(
        modifier = modifier,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedCount > 0) {
                    OutlinedButton(
                        onClick = onClearClick,
                        modifier = Modifier.height(height),
                        shape = shape
                    ) {
                        Text(
                            text = stringResource(id = R.string.clear_selection_button),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                Button(
                    onClick = onCheckClick,
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .height(height),
                    shape = shape
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.check_game_button),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
