package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.usecase.FrequencyAnalysis
import com.cebolao.lotofacil.domain.usecase.GetFrequencyAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.GetPatternAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.GetTrendAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.PatternAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class InsightsUiState(
    val isLoading: Boolean = false,
    val frequencyAnalysis: FrequencyAnalysis? = null,
    val patternAnalysis: PatternAnalysis? = null,
    val trendAnalysis: TrendAnalysis? = null,
    val selectedPatternSize: Int = 2,
    val selectedTrendType: TrendType = TrendType.SUM,
    val selectedTrendWindow: Int = 50,
    val error: String? = null
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getFrequencyAnalysisUseCase: GetFrequencyAnalysisUseCase,
    private val getPatternAnalysisUseCase: GetPatternAnalysisUseCase,
    private val getTrendAnalysisUseCase: GetTrendAnalysisUseCase
) : StateViewModel<InsightsUiState>(InsightsUiState()) {

    init {
        loadFrequencyAnalysis()
        loadPatternAnalysis(2)
        loadTrendAnalysis(TrendType.SUM, 50)
    }

    fun loadFrequencyAnalysis() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            when (val result = getFrequencyAnalysisUseCase()) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false, frequencyAnalysis = result.value) }
                }
                is AppResult.Failure -> {
                    updateState { it.copy(isLoading = false, error = "Falha ao carregar análise de frequência") }
                }
            }
        }
    }

    fun onPatternSizeSelected(size: Int) {
        if (uiState.value.selectedPatternSize == size) return
        updateState { it.copy(selectedPatternSize = size) }
        loadPatternAnalysis(size)
    }

    private fun loadPatternAnalysis(size: Int) {
        viewModelScope.launch {
            when (val result = getPatternAnalysisUseCase(size)) {
                is AppResult.Success -> {
                    updateState { it.copy(patternAnalysis = result.value) }
                }
                is AppResult.Failure -> {
                    updateState { it.copy(error = "Falha ao carregar análise de padrões") }
                }
            }
        }
    }

    fun onTrendTypeSelected(type: TrendType) {
        if (uiState.value.selectedTrendType == type) return
        updateState { it.copy(selectedTrendType = type) }
        loadTrendAnalysis(type, uiState.value.selectedTrendWindow)
    }

    fun onTrendWindowSelected(window: Int) {
        if (uiState.value.selectedTrendWindow == window) return
        updateState { it.copy(selectedTrendWindow = window) }
        loadTrendAnalysis(uiState.value.selectedTrendType, window)
    }

    private fun loadTrendAnalysis(type: TrendType, windowSize: Int) {
        viewModelScope.launch {
            when (val result = getTrendAnalysisUseCase(type, windowSize)) {
                is AppResult.Success -> {
                    updateState { it.copy(trendAnalysis = result.value) }
                }
                is AppResult.Failure -> {
                    updateState { it.copy(error = "Falha ao carregar análise de tendências") }
                }
            }
        }
    }
}
