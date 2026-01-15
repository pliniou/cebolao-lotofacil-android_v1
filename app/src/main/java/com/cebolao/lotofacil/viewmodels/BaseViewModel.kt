package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing common functionality for UI events, navigation, and snackbars.
 */
abstract class BaseViewModel : ViewModel() {
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    protected fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    protected fun showSnackbar(message: String, actionLabel: String? = null) {
        sendUiEvent(UiEvent.ShowSnackbar(message = message, actionLabel = actionLabel))
    }

    protected fun showSnackbar(
        @StringRes messageResId: Int,
        @StringRes actionLabelResId: Int? = null
    ) {
        sendUiEvent(
            UiEvent.ShowSnackbar(
                messageResId = messageResId,
                actionLabelResId = actionLabelResId
            )
        )
    }

    protected fun navigate(destination: Destination) {
        sendUiEvent(UiEvent.Navigate(destination))
    }

    protected fun navigateBack(route: String? = null) {
        sendUiEvent(UiEvent.NavigateBack(route))
    }

    protected fun navigateUp() {
        sendUiEvent(UiEvent.NavigateUp)
    }

    protected fun navigateToGeneratedGames() {
        sendUiEvent(UiEvent.NavigateToGeneratedGames)
    }

    protected fun showResetConfirmation() {
        sendUiEvent(UiEvent.ShowResetConfirmation)
    }
}

/**
 * Modern state-holding ViewModel following Unidirectional Data Flow.
 */
abstract class StateViewModel<S>(initialState: S) : BaseViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected val currentState: S get() = _uiState.value

    protected fun updateState(update: (S) -> S) {
        _uiState.update(update)
    }
}
