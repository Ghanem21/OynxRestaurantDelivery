package com.androidghanem.data.session

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Manages automatic session expiration after a period of inactivity
 */
class SessionExpirationManager(
    context: Context,
    private val sessionManager: SessionManager,
) : Application.ActivityLifecycleCallbacks {

    companion object {
        // Session timeout after 2 minutes of inactivity
        private val SESSION_TIMEOUT_DURATION = TimeUnit.MINUTES.toMillis(2)
        private const val TAG = "SessionExpiration"
        
        // Listener interface for session expiration
        interface SessionExpirationListener {
            fun onSessionExpired()
        }
    }

    // Create a CoroutineScope with SupervisorJob to prevent cancellation cascade
    private val sessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var expirationJob: Job? = null
    
    private var lastUserInteractionTime: Long = System.currentTimeMillis()
    private var lastBackgroundTime: Long = 0L
    private var isAppInForeground = false
    private var sessionExpirationListener: SessionExpirationListener? = null

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(this)
        Log.i(TAG, "SessionExpirationManager initialized with timeout of ${SESSION_TIMEOUT_DURATION / 1000} seconds")
        startSessionMonitoring()
    }
    
    /**
     * Sets a listener to be notified when the session expires
     */
    fun setSessionExpirationListener(listener: SessionExpirationListener) {
        this.sessionExpirationListener = listener
        Log.d(TAG, "Session expiration listener set")
    }

    /**
     * Start coroutine monitoring for session expiration
     */
    private fun startSessionMonitoring() {
        Log.d(TAG, "Starting session expiration monitoring coroutine")
        cancelExistingMonitoring()

        expirationJob = sessionScope.launch {
            while (isActive) {
                // Only check when the app is in foreground and user is logged in
                if (isAppInForeground && sessionManager.isLoggedIn.value) {
                    val currentTime = System.currentTimeMillis()
                    val inactiveTime = currentTime - lastUserInteractionTime

                    if (inactiveTime >= SESSION_TIMEOUT_DURATION) {
                        Log.i(
                            TAG,
                            "Session expired after ${inactiveTime / 1000} seconds of inactivity"
                        )
                        sessionManager.clearSession()

                        // Execute on main thread
                        launch(Dispatchers.Main) {
                            sessionExpirationListener?.onSessionExpired()
                            Log.i(TAG, "Session expiration callback completed")
                        }

                        // Break the loop after expiring the session
                        break
                    } else {
                        // Calculate time remaining until session expires
                        val remainingTime = SESSION_TIMEOUT_DURATION - inactiveTime
                        val nextCheckDelay = when {
                            remainingTime < 5000 -> 1000L // Check every second when close to timeout
                            remainingTime < 30000 -> 5000L // Check every 5 seconds when within 30 seconds
                            else -> 10000L // Otherwise check every 10 seconds
                        }
                        Log.v(
                            TAG,
                            "Session active, ${remainingTime / 1000}s remaining, next check in ${nextCheckDelay / 1000}s"
                        )
                        delay(nextCheckDelay)
                    }
                } else {
                    // Wait a bit before checking again
                    delay(5000L)
                }
            }
        }
    }

    /**
     * Cancel any existing monitoring jobs
     */
    private fun cancelExistingMonitoring() {
        expirationJob?.cancel()
        expirationJob = null
    }

    /**
     * Sets the app foreground state
     */
    fun setAppForegroundState(inForeground: Boolean) {
        val stateChanged = isAppInForeground != inForeground
        
        if (stateChanged) {
            Log.d(TAG, "App foreground state changed to: ${if (inForeground) "foreground" else "background"}")
            
            if (inForeground) {
                // App coming to foreground
                val currentTime = System.currentTimeMillis()
                
                // If we have a record of when the app went to background
                if (lastBackgroundTime > 0) {
                    val backgroundDuration = currentTime - lastBackgroundTime
                    Log.d(TAG, "App was in background for ${backgroundDuration / 1000} seconds")
                    
                    // If the app was in background for longer than session timeout
                    if (backgroundDuration >= SESSION_TIMEOUT_DURATION && sessionManager.isLoggedIn.value) {
                        Log.i(TAG, "Session expired while app was in background for ${backgroundDuration / 1000} seconds")
                        sessionManager.clearSession()

                        sessionScope.launch(Dispatchers.Main) {
                            sessionExpirationListener?.onSessionExpired()
                            Log.i(TAG, "Background session expiration callback completed")
                        }
                        
                        // Reset background time
                        lastBackgroundTime = 0
                        isAppInForeground = inForeground
                        return
                    }
                }

                // If session hasn't expired, start monitoring again
                resetInactivityTimer()
                startSessionMonitoring()
                lastBackgroundTime = 0
            } else {
                // App going to background
                lastBackgroundTime = System.currentTimeMillis()
                cancelExistingMonitoring()
            }
        }
        
        isAppInForeground = inForeground
    }

    /**
     * Resets the inactivity timer when user interacts with the app
     */
    fun resetInactivityTimer() {
        lastUserInteractionTime = System.currentTimeMillis()

        Log.v(
            TAG,
            "Inactivity timer reset, session alive for another ${SESSION_TIMEOUT_DURATION / 1000} seconds"
        )
    }

    // Application.ActivityLifecycleCallbacks implementation
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    
    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "Activity started: ${activity.javaClass.simpleName}")
        setAppForegroundState(true)
    }
    
    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "Activity resumed: ${activity.javaClass.simpleName}")
        resetInactivityTimer()
    }
    
    override fun onActivityPaused(activity: Activity) {}
    
    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "Activity stopped: ${activity.javaClass.simpleName}")
        setAppForegroundState(false)
    }
    
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    
    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Called when the application is shutting down
     */
    fun shutdown() {
        Log.d(TAG, "Shutting down session expiration manager")
        sessionScope.cancel()
    }
}