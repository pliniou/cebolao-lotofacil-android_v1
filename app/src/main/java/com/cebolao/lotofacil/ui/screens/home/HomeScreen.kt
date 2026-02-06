package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.constants.AppConstants
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.SnackbarHost
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconButtonSize
import com.cebolao.lotofacil.ui.components.ClickableCard
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import com.cebolao.lotofacil.ui.theme.iconExtraLarge
import com.cebolao.lotofacil.ui.theme.iconMedium
import com.cebolao.lotofacil.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onExploreFilters: () -> Unit = {},
    onOpenChecker: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(homeViewModel, resources) {
        homeViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val resolvedMessage = event.message ?: event.messageResId?.let { messageResId ->
                        resources.getString(messageResId)
                    }
                    if (!resolvedMessage.isNullOrBlank()) {
                        snackbarHostState.showSnackbar(
                            message = resolvedMessage,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.cebolao_title),
                subtitle = stringResource(id = R.string.lotofacil_subtitle),
                iconPainter = painterResource(id = R.drawable.ic_cebolalogo),
                actions = {
                    RefreshButton(
                        isRefreshing = uiState.isRefreshing,
                        onClick = { homeViewModel.refreshData() }
                    )
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
                top = AppSpacing.lg,
                start = AppSpacing.lg,
                end = AppSpacing.lg,
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
        ) {
            when {
                uiState.isScreenLoading -> {
                    item(key = "loading") {
                        HomeScreenLoadingState()
                    }
                }
                uiState.errorMessageResId != null -> {
                    item(key = "error") {
                        ErrorState(messageResId = uiState.errorMessageResId) {
                            homeViewModel.refreshData()
                        }
                    }
                }
                else -> {
                    item(key = "welcome_banner") {
                        WelcomeBanner(
                            lastUpdateTime = uiState.lastUpdateTime,
                            nextDrawDate = uiState.nextDrawDate,
                            nextDrawContest = uiState.nextDrawContest,
                            isTodayDrawDay = uiState.isTodayDrawDay,
                            onExploreFilters = onExploreFilters,
                            onOpenChecker = onOpenChecker
                        )
                    }
                    item(key = "last_draw") {
                        uiState.lastDrawStats?.let { stats ->
                            AnimateOnEntry(delayMillis = AppConstants.ANIMATION_DURATION_SHORT) { LastDrawSection(stats) }
                        }
                    }
                    item(key = "statistics") {
                        AnimateOnEntry(delayMillis = AppConstants.ANIMATION_DURATION_MEDIUM) {
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
                    item(key = "advanced_stats_card") {
                        AnimateOnEntry(delayMillis = AppConstants.ANIMATION_DURATION_MEDIUM + 100) {
                            AdvancedStatsCard(onClick = onNavigateToInsights)
                        }
                    }
                    item(key = "frequency_summary") {
                        AnimateOnEntry(delayMillis = AppConstants.ANIMATION_DURATION_MEDIUM + 200) {
                            FrequencyChartSection()
                        }
                    }
                    item(key = "explanation") {
                        AnimateOnEntry(delayMillis = AppConstants.ANIMATION_DURATION_LONG) { StatisticsExplanationCard() }
                    }
                }
            }
        }
    }
}

@Composable
private fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    // Optimize refresh animation with modernized duration
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = tween(
            durationMillis = AppConstants.ANIMATION_DURATION_SHORT.toInt(),
            easing = androidx.compose.animation.core.LinearEasing
        ),
        label = "refresh_rotation"
    )

    IconButton(
        onClick = onClick,
        enabled = !isRefreshing,
        modifier = Modifier.size(iconButtonSize())
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = stringResource(id = R.string.cd_refresh_data),
            tint = if (isRefreshing) colors.outline else colors.primary,
            modifier = Modifier
                .size(iconMedium())
                .graphicsLayer { rotationZ = rotationAngle }
        )
    }
}

@Composable
private fun HomeScreenLoadingState() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        WelcomeBanner(
            lastUpdateTime = null,
            nextDrawDate = null,
            nextDrawContest = null,
            isTodayDrawDay = false,
            onExploreFilters = {},
            onOpenChecker = {}
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                CircularProgressIndicator(
                    color = colors.primary,
                    modifier = Modifier.size(iconExtraLarge())
                )
                Text(
                    text = stringResource(id = R.string.loading_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ErrorState(messageResId: Int?, onRetry: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    AppCard(modifier = Modifier.fillMaxWidth(), backgroundColor = colors.surface) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = colors.error,
                modifier = Modifier.size(iconExtraLarge())
            )
            Text(
                text = messageResId?.let { stringResource(id = it) } ?: stringResource(id = R.string.error_unknown),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = colors.onSurface,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = AppElevation.sm)
            ) {
                Text(
                    text = stringResource(id = R.string.try_again),
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AdvancedStatsCard(onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    ClickableCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundColor = colors.primaryContainer.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.advanced_stats_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onPrimaryContainer
                )
                Text(
                    text = stringResource(id = R.string.advanced_stats_card_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = colors.primary.copy(alpha = 0.5f)
            )
        }
    }
}


