package com.cebolao.lotofacil.ui.screens.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    viewModel: UserStatsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.nav_user_stats),
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                uiState.isLoading -> {
                    LoadingData(message = stringResource(id = R.string.loading_data))
                }
                uiState.error != null -> {
                    ErrorCard(
                        message = uiState.error!!,
                        modifier = Modifier.padding(AppSpacing.lg),
                        actions = { viewModel.loadStats() }
                    )
                }
                uiState.stats != null && uiState.stats!!.totalGamesGenerated > 0 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(AppSpacing.lg),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.lg)
                    ) {
                        item {
                            UserStatsSection(stats = uiState.stats!!)
                        }
                    }
                }
                else -> {
                    EmptyState(
                        messageResId = R.string.stats_empty_message,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
