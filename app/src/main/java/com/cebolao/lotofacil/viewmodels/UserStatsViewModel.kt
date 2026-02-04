package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.service.UserStats
import com.cebolao.lotofacil.domain.usecase.GetUserGameStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class UserStatsUiState(
    val isLoading: Boolean = false,
    val stats: UserStats? = null,
    val error: String? = null
)

@HiltViewModel
class UserStatsViewModel @Inject constructor(
    private val getUserGameStatisticsUseCase: GetUserGameStatisticsUseCase,
    private val gameRepository: GameRepository
) : StateViewModel<UserStatsUiState>(UserStatsUiState()) {

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            when (val result = getUserGameStatisticsUseCase()) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false, stats = result.value) }
                }
                is AppResult.Failure -> {
                    updateState { it.copy(isLoading = false, error = "Falha ao carregar estatísticas") }
                }
            }
        }
    }

    fun recordGameUsage(gameId: String) {
        viewModelScope.launch {
            gameRepository.recordGameUsage(gameId)
            loadStats() // Refresh stats after recording usage
        }
    }
}
