package com.cebolao.lotofacil.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost as MaterialSnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Consistent SnackbarHost component with standardized styling.
 * Provides uniform appearance and behavior across the app.
 */
@Composable
fun SnackbarHost(
    hostState: SnackbarHostState = SnackbarHostState(),
    modifier: Modifier = Modifier
) {
    MaterialSnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                shape = MaterialTheme.shapes.medium,
                containerColor = when {
                    snackbarData.visuals.actionLabel != null -> {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                    else -> {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                },
                contentColor = when {
                    snackbarData.visuals.actionLabel != null -> {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                    else -> {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                },
                actionColor = MaterialTheme.colorScheme.primary,
                actionContentColor = MaterialTheme.colorScheme.onPrimary,
                dismissActionContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
            )
        }
    )
}
