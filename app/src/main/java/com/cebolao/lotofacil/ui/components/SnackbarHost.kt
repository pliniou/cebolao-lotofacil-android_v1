package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.LocalAppColors
import androidx.compose.material3.SnackbarHost as MaterialSnackbarHost

/**
 * Consistent SnackbarHost component with standardized styling.
 * Provides uniform appearance and behavior across the app.
 */
@Composable
fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    MaterialSnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { snackbarData: SnackbarData ->
            val hasAction = snackbarData.visuals.actionLabel != null
            Snackbar(
                snackbarData = snackbarData,
                shape = MaterialTheme.shapes.medium,
                containerColor = if (hasAction) colors.brandSubtle else colors.surface2,
                contentColor = colors.textPrimary,
                actionColor = colors.brandPrimary,
                actionContentColor = colors.background,
                dismissActionContentColor = colors.textSecondary,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = colors.outline.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.medium
                )
            )
        }
    )
}
