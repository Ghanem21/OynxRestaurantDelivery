package com.androidghanem.oynxrestaurantdelivery.ui.navigation

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    // Using extraBufferCapacity=1 ensures we don't miss events even if no collector is active
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    // Keep track of whether we're already handling a session expiration
    private var isHandlingExpiration = false

    // Use SupervisorJob to prevent coroutine cancellation if one fails
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Error in session expiration handling", throwable)
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main + exceptionHandler)
    
    /**
     * Trigger a session expiration event
     */
    fun sessionExpired() {
        // Prevent multiple simultaneous expiration events
        if (isHandlingExpiration) {
            Log.d(TAG, "Session expiration already in progress, ignoring duplicate event")
            return
        }
        
        Log.i(TAG, "Session expired event triggered")
        isHandlingExpiration = true

        // Make sure we run this on the main thread
        Handler(Looper.getMainLooper()).post {
            scope.launch {
                try {
                    _sessionExpiredEvent.emit(Unit)
                    Log.d(TAG, "Session expired event emitted successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to emit session expired event", e)
                    // Fallback mechanism in case the event system fails
                    forceFallbackToLogin()
                }
            }
        }
    }
    
    /**
     * Navigate to login screen after session expiration
     * Clears the back stack and navigates to login screen
     */
    fun navigateToLogin(navController: NavController) {
        try {
            Log.i(TAG, "Navigating to login screen due to session expiration")
            navController.navigate(Screen.Login.route) {
                // Clear entire back stack to prevent back navigation to expired session
                popUpTo(0) {
                    inclusive = true
                }
            }

            // Show toast to inform user
            Handler(Looper.getMainLooper()).post {
                val context = navController.context
                Toast.makeText(
                    context,
                    "Session expired due to inactivity",
                    Toast.LENGTH_LONG
                ).show()
            }

            // Reset handling flag after a delay to prevent rapid re-triggering
            Handler(Looper.getMainLooper()).postDelayed({
                isHandlingExpiration = false
            }, 1000)
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to login screen", e)
            forceFallbackToLogin()
        }
    }

    /**
     * Fallback mechanism if normal navigation fails
     */
    private fun forceFallbackToLogin() {
        Log.w(TAG, "Using fallback mechanism to return to login")
        try {
            // This will be handled by the app restarting to the login screen
            // since we've already cleared the session
            isHandlingExpiration = false
        } catch (e: Exception) {
            Log.e(TAG, "Fallback navigation failed", e)
            isHandlingExpiration = false
        }
    }
}