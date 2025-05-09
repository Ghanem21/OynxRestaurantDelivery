package com.androidghanem.oynxrestaurantdelivery.ui.navigation

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Central handler for session expiration events to trigger navigation.
 * This uses a singleton pattern to ensure the event can be triggered from anywhere
 * and received by the active navigation controller.
 */
object SessionExpirationHandler {
    private const val TAG = "SessionExpiration"
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()
    
    /**
     * Trigger a session expiration event
     */
    fun sessionExpired() {
        Log.i(TAG, "Session expired event triggered")
        CoroutineScope(Dispatchers.Main).launch {
            _sessionExpiredEvent.emit(Unit)
        }
    }
    
    /**
     * Navigate to login screen after session expiration
     */
    fun navigateToLogin(navController: NavController) {
        Log.i(TAG, "Navigating to login screen due to session expiration")
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }
}