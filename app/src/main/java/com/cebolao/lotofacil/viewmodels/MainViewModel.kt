package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.constants.AppConstants
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
class MainViewModel @Inject constructor() : StateViewModel<MainUiState>(MainUiState()) {

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                // Simulating initialization (like loading preferences or checking local DB)
                delay(AppConstants.ANIMATION_DURATION_SPLASH)
                updateState { it.copy(isLoading = false) }
            } catch (_: Exception) {
                updateState {
                    it.copy(isLoading = false, hasError = true, errorMessage = "Failed to initialize the application.")
                }
            }
        }
    }
}
