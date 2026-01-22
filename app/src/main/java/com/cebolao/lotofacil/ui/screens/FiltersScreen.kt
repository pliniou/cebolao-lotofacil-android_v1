package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.model.descriptionRes
import com.cebolao.lotofacil.ui.model.icon
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.FiltersViewModel

@Composable
fun FiltersScreen(
    filtersViewModel: FiltersViewModel = hiltViewModel(),
    onNavigateToGeneratedGames: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by filtersViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDialogFor by remember { mutableStateOf<FilterType?>(null) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        filtersViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateToGeneratedGames -> onNavigateToGeneratedGames()
                is UiEvent.ShowSnackbar -> {
                    val message = event.message ?: event.messageResId?.let(context::getString).orEmpty()
                    if (message.isNotBlank()) {
                        val actionLabel = event.actionLabel ?: event.actionLabelResId?.let(context::getString)
                        snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel, withDismissAction = true)
                    }
                }
                is UiEvent.ShowResetConfirmation -> showResetConfirmation = true
                is UiEvent.Navigate -> {}
                is UiEvent.NavigateBack -> {}
                is UiEvent.NavigateUp -> {}
            }
        }
    }

    // Filter info dialog
    showDialogFor?.let { type ->
        InfoDialog(
            onDismissRequest = { showDialogFor = null },
            dialogTitle = stringResource(type.titleRes),
            icon = type.icon
        ) {
            Text(stringResource(type.descriptionRes))
        }
    }

    // Reset confirmation dialog
    if (showResetConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.reset_filters_title),
            message = stringResource(id = R.string.reset_filters_message),
            confirmText = stringResource(id = R.string.reset_button),
            dismissText = stringResource(id = R.string.cancel_button),
            onConfirm = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                filtersViewModel.confirmResetAllFilters()
                showResetConfirmation = false
            },
            onDismiss = { showResetConfirmation = false }
        )
    }

    androidx.compose.material3.Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            FiltersHeader(
                onResetFilters = { filtersViewModel.requestResetAllFilters() }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.xl)
        ) {

            item {
                ActiveFiltersPanel(
                    activeFilters = uiState.filterStates.filter { it.isEnabled },
                    successProbability = uiState.successProbability
                )
            }

            // Apply single animation to filter items
            item {
                AnimateOnEntry(delayMillis = 200L) {
                    Column(
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.lg)
                    ) {
                        // Filter items will be added here
                    }
                }
            }

            // Add filter items with animation
            filterList(
                filterStates = uiState.filterStates,
                lastDraw = uiState.lastDraw,
                onFilterToggle = { type, enabled -> filtersViewModel.onFilterToggle(type, enabled) },
                onRangeChange = { type, range -> filtersViewModel.onRangeAdjust(type, range) },
                onInfoClick = { type -> showDialogFor = type }
            )

            item {
                GenerateActionsPanel(
                    generationState = uiState.generationState,
                    onGenerate = { quantity -> filtersViewModel.generateGames(quantity) }
                )
            }
        }
    }
}
