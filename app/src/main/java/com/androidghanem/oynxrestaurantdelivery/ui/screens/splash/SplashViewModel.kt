package com.androidghanem.oynxrestaurantdelivery.ui.screens.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.oynxrestaurantdelivery.OnyxApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for splash screen that handles initialization and checks user session
 */
class SplashViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appInstance: OnyxApplication = application as OnyxApplication
    private val sessionManager = appInstance.sessionManager
    
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    
    init {
        checkUserSession()
    }
    
    /**
     * Check if the user is already logged in
     */
    private fun checkUserSession() {
        viewModelScope.launch {
            // Collect once from the session manager
            val isLoggedIn = sessionManager.isLoggedIn.value
            _uiState.value = SplashUiState(
                isInitialized = true,
                isLoggedIn = isLoggedIn
            )
        }
    }
    
    fun onSplashFinished() {
        _uiState.value = _uiState.value.copy(isSplashFinished = true)
    }
}

/**
 * UI state for the splash screen
 */
data class SplashUiState(
    val isInitialized: Boolean = false,
    val isSplashFinished: Boolean = false,
    val isLoggedIn: Boolean = false
)