package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.screens.home.HomeScreenSkeleton
import com.cebolao.lotofacil.ui.screens.home.LastDrawSection
import com.cebolao.lotofacil.ui.screens.home.StatisticsExplanationCard
import com.cebolao.lotofacil.ui.screens.home.StatisticsSection
import com.cebolao.lotofacil.ui.screens.home.WelcomeBanner
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconExtraLarge
import com.cebolao.lotofacil.ui.theme.LocalAppColors
import com.cebolao.lotofacil.viewmodels.HomeViewModel

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val context = LocalContext.current
    var showRefreshConfirmation by remember { mutableStateOf(false) }
    val colors = LocalAppColors.current

    LaunchedEffect(Unit) {
        homeViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val message = event.message ?: event.messageResId?.let(context::getString).orEmpty()
                    if (message.isNotBlank()) {
                        val actionLabel = event.actionLabel ?: event.actionLabelResId?.let(context::getString)
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = actionLabel,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.cebolao_title),
                subtitle = stringResource(id = R.string.lotofacil_subtitle),
                iconPainter = painterResource(id = R.drawable.logo_cebola),
                actions = {
                    IconButton(onClick = { showRefreshConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.cd_refresh_data),
                            tint = colors.brandPrimary
                        )
                    }
                }
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
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            if (uiState.isScreenLoading) {
                item(key = "skeleton") { HomeScreenSkeleton() }
            } else if (uiState.errorMessageResId != null) {
                item(key = "error") {
                    ErrorState(
                        messageResId = uiState.errorMessageResId!!,
                        onRetry = { homeViewModel.retryInitialLoad() }
                    )
                }
            } else {
                item(key = "welcome_banner") {
                    WelcomeBanner(
                        lastUpdateTime = uiState.lastUpdateTime,
                        onExploreFilters = { /* Navigate to filters */ },
                        onOpenChecker = { /* Navigate to checker */ }
                    )
                }
                item(key = "last_draw") {
                    uiState.lastDrawStats?.let { stats ->
                        AnimateOnEntry(delayMillis = 100L) {
                            LastDrawSection(stats)
                        }
                    }
                }
                item(key = "statistics") {
                    AnimateOnEntry(delayMillis = 200L) {
                        StatisticsSection(
                            stats = uiState.statistics,
                            isStatsLoading = uiState.isStatsLoading,
                            selectedTimeWindow = uiState.selectedTimeWindow,
                            selectedPattern = uiState.selectedPattern,
                            onTimeWindowSelected = { homeViewModel.onTimeWindowSelected(it) },
                            onPatternSelected = { homeViewModel.onPatternSelected(it) }
                        )
                    }
                }
                item(key = "explanation") {
                    AnimateOnEntry(delayMillis = 300L) {
                        StatisticsExplanationCard()
                    }
                }
            }
        }
    }

    // Refresh confirmation dialog
    if (showRefreshConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.refresh_confirmation_title),
            message = stringResource(id = R.string.refresh_confirmation_message),
            confirmText = stringResource(id = R.string.confirm_button),
            dismissText = stringResource(id = R.string.cancel_button),
            onConfirm = {
                showRefreshConfirmation = false
                homeViewModel.refreshData()
            },
            onDismiss = { showRefreshConfirmation = false }
        )
    }
}

@Composable
private fun ErrorState(messageResId: Int, onRetry: () -> Unit) {
    val colors = LocalAppColors.current
    
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg),
        backgroundColor = colors.error
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = stringResource(id = R.string.cd_error_state),
                modifier = Modifier.size(iconExtraLarge()),
                tint = colors.background
            )
            Text(
                text = stringResource(id = R.string.error_load_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.background
            )
            Text(
                text = stringResource(messageResId),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.background,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(id = R.string.try_again))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(id = R.string.try_again))
            }
        }
    }
}
