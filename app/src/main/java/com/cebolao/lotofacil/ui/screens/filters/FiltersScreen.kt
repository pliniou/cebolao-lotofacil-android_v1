package com.cebolao.lotofacil.ui.screens.filters

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
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
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.SnackbarHost
import com.cebolao.lotofacil.ui.model.descriptionRes
import com.cebolao.lotofacil.ui.model.icon
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.FiltersViewModel
import com.cebolao.lotofacil.ui.components.AppCard
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.components.shimmer

@Composable
fun FiltersScreen(
    modifier: Modifier = Modifier,
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

    // Delegate to Content composable
    FiltersScreenContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        haptic = haptic,
        modifier = modifier,
        showDialogFor = showDialogFor,
        onShowDialog = { showDialogFor = it },
        showResetConfirmation = showResetConfirmation,
        onShowResetConfirmation = { showResetConfirmation = it },
        onAction = { action ->
            when (action) {
                is FiltersAction.RequestResetAllFilters -> filtersViewModel.requestResetAllFilters()
                is FiltersAction.ConfirmResetAllFilters -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    filtersViewModel.confirmResetAllFilters()
                    showResetConfirmation = false
                }
                is FiltersAction.OnFilterToggle -> filtersViewModel.onFilterToggle(action.type, action.enabled)
                is FiltersAction.OnRangeChange -> filtersViewModel.onRangeAdjust(action.type, action.range)
                is FiltersAction.GenerateGames -> filtersViewModel.generateGames(action.quantity)
            }
        }
    )
}

// ==================== SEALED ACTIONS ====================
sealed class FiltersAction {
    object RequestResetAllFilters : FiltersAction()
    object ConfirmResetAllFilters : FiltersAction()
    data class OnFilterToggle(val type: FilterType, val enabled: Boolean) : FiltersAction()
    data class OnRangeChange(val type: FilterType, val range: IntRange) : FiltersAction()
    data class GenerateGames(val quantity: Int) : FiltersAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun FiltersScreenContent(
    state: FiltersUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    haptic: HapticFeedback = LocalHapticFeedback.current,
    modifier: Modifier = Modifier,
    showDialogFor: FilterType? = null,
    onShowDialog: (FilterType?) -> Unit = {},
    showResetConfirmation: Boolean = false,
    onShowResetConfirmation: (Boolean) -> Unit = {},
    onAction: (FiltersAction) -> Unit = {}
) {
    // Derived state para evitar recomposições desnecessárias
    val activeFiltersCount by remember(state.filterStates) { 
        derivedStateOf { state.filterStates.count { it.isEnabled } } 
    }
    val successProbability by remember(state.successProbability) { 
        derivedStateOf { state.successProbability } 
    }

    // Filter info dialog
    showDialogFor?.let { type ->
        InfoDialog(
            onDismissRequest = { onShowDialog(null) },
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
            onConfirm = { onAction(FiltersAction.ConfirmResetAllFilters) },
            onDismiss = { onShowResetConfirmation(false) }
        )
    }

    androidx.compose.material3.Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            FiltersHeader(
                onResetFilters = { onAction(FiltersAction.RequestResetAllFilters) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                top = AppSpacing.lg,
                start = AppSpacing.lg,
                end = AppSpacing.lg,
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.xl)
        ) {

            item(key = "active_filters") {
                ActiveFiltersPanel(
                    activeFilters = state.filterStates.filter { it.isEnabled },
                    successProbability = successProbability
                )
            }

            if (state.lastDraw == null) {
                items(3) {
                    FilterCardSkeleton()
                }
            } else {
                // Add filter items
                filterList(
                    filterStates = state.filterStates,
                    lastDraw = state.lastDraw,
                    onFilterToggle = { type, enabled -> 
                        onAction(FiltersAction.OnFilterToggle(type, enabled)) 
                    },
                    onRangeChange = { type, range -> 
                        onAction(FiltersAction.OnRangeChange(type, range)) 
                    },
                    onInfoClick = { type -> onShowDialog(type) }
                )
            }

            item(key = "generate_actions") {
                GenerateActionsPanel(
                    generationState = state.generationState,
                    onGenerate = { quantity -> onAction(FiltersAction.GenerateGames(quantity)) }
                )
            }
        }
    }
}

@Composable
private fun FilterCardSkeleton() {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shimmer(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        // Shimmering content
    }
}
