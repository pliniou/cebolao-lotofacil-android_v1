package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.constants.AppConstants
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class MainUiState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessage: String? = null
) {
    val isReady: Boolean get() = !isLoading && !hasError
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : StateViewModel<MainUiState>(MainUiState()) {

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            // Wait for both minimum splash duration and data initialization
            val startTime = System.currentTimeMillis()
            
            // Wait for DB to be ready
            historyRepository.isInitialized.collect { isReady ->
                if (isReady) {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    val remainingTime = AppConstants.ANIMATION_DURATION_SPLASH - elapsedTime
                    if (remainingTime > 0) {
                        delay(remainingTime)
                    }
                    updateState { it.copy(isLoading = false) }
                    // stop collecting
                    return@collect
                }
            }
        }
    }
}
