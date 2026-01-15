package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel class that provides common functionality for all ViewModels in the app.
 * Includes optimized state management, job cancellation, and error handling.
 */
abstract class BaseViewModel : ViewModel() {
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    
    /**
     * Protected coroutine scope for launching coroutines in this ViewModel
     */
    protected val scope: CoroutineScope
        get() = viewModelScope
    
    /**
     * Map to keep track of active jobs for proper cancellation
     */
    private val activeJobs = mutableMapOf<String, Job>()
    
    /**
     * Launches a coroutine with automatic job tracking and cancellation
     */
    protected fun launchCancellable(
        key: String? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        // Cancel existing job with same key if exists
        key?.let { activeJobs[it]?.cancel() }
        
        val job = viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                // Handle exceptions if needed
                throw e
            }
        }
        
        // Track the job if key is provided
        key?.let { activeJobs[it] = job }
        
        // Remove job from tracking when completed
        job.invokeOnCompletion { 
            key?.let { activeJobs.remove(it) }
        }
        
        return job
    }
    
    /**
     * Cancels all active jobs
     */
    protected fun cancelAllJobs() {
        activeJobs.values.forEach { it.cancel() }
        activeJobs.clear()
    }
    
    /**
     * Cancels a specific job by key
     */
    protected fun cancelJob(key: String) {
        activeJobs[key]?.cancel()
        activeJobs.remove(key)
    }
    
    override fun onCleared() {
        super.onCleared()
        cancelAllJobs()
    }

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
 * Generic state holder for ViewModels with immutable StateFlow exposure
 */
abstract class StateViewModel<T> : BaseViewModel() {
    
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<T> = _state.asStateFlow()
    
    /**
     * Provide the initial state for the ViewModel
     */
    protected abstract fun initialState(): T
    
    /**
     * Update the state in a thread-safe manner
     */
    protected fun updateState(block: (T) -> T) {
        _state.update(block)
    }
    
    /**
     * Get current state value (use sparingly, prefer using state Flow)
     */
    protected val currentState: T
        get() = _state.value
}
