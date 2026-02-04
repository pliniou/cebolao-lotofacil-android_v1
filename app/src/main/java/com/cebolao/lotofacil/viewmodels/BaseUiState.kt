package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes

/**
 * Base interface for all UI states, providing common loading and error states.
 * This ensures consistency across all ViewModel state implementations.
 */
interface BaseUiState {
    val isLoading: Boolean get() = false
    val hasError: Boolean get() = false
    val errorMessageResId: Int? get() = null
    val errorMessage: String? get() = null
}

/**
 * Base data class for UI states with common loading and error properties.
 * ViewModels should extend this for consistent state management.
 */
abstract class BaseUiStateData(
    override open val isLoading: Boolean = false,
    override open val hasError: Boolean = false,
    @StringRes override open val errorMessageResId: Int? = null,
    override open val errorMessage: String? = null
) : BaseUiState {
    
    val isReady: Boolean get() = !isLoading && !hasError
    val hasLoadingState: Boolean get() = isLoading
    val hasErrorState: Boolean get() = hasError
}
