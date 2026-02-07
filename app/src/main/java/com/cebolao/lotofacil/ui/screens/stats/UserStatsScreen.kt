package com.cebolao.lotofacil.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.EmptyState
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.LoadingData
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.UserStatsViewModel

@Composable
fun UserStatsScreen(
    modifier: Modifier = Modifier,
    viewModel: UserStatsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Delegate to Content composable
    UserStatsScreenContent(
        state = uiState,
        modifier = modifier,
        onNavigateBack = onNavigateBack,
        onAction = { action ->
            when (action) {
                is UserStatsAction.LoadStats -> viewModel.loadStats()
            }
        }
    )
}

// ==================== SEALED ACTIONS ====================
sealed class UserStatsAction {
    object LoadStats : UserStatsAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun UserStatsScreenContent(
    state: UserStatsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAction: (UserStatsAction) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.nav_user_stats),
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingData(
                    message = stringResource(id = R.string.loading_data),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
<<<<<<< HEAD
            state.errorMessageResId != null -> {
                ErrorCard(
                    messageResId = state.errorMessageResId!!,
                    modifier = Modifier.padding(innerPadding).padding(AppSpacing.lg),
                    actions = { onAction(UserStatsAction.LoadStats) }
                )
            }
            state.stats != null && state.stats!!.totalGamesGenerated > 0 -> {
=======

            uiState.errorMessageResId != null -> {
                ErrorCard(
                    messageResId = uiState.errorMessageResId!!,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(AppSpacing.lg),
                    actions = { viewModel.loadStats() }
                )
            }

            uiState.stats != null && uiState.stats!!.totalGamesGenerated > 0 -> {
>>>>>>> 7ff0420c3946b4c7d18d48cef0333dc484501e7f
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(AppSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    item(key = "user_stats_section") {
                        UserStatsSection(stats = state.stats!!)
                    }
                }
            }

            else -> {
                EmptyState(
                    messageResId = R.string.stats_empty_message,
<<<<<<< HEAD
                    modifier = Modifier.fillMaxSize()
=======
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
>>>>>>> 7ff0420c3946b4c7d18d48cef0333dc484501e7f
                )
            }
        }
    }
}
