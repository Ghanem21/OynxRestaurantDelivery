package com.androidghanem.data.session

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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

    private val handler = Handler(Looper.getMainLooper())
    private var lastUserInteractionTime: Long = System.currentTimeMillis()
    private var lastBackgroundTime: Long = 0L
    private var isAppInForeground = false
    private var sessionExpirationListener: SessionExpirationListener? = null

    private val sessionTimeoutRunnable = Runnable {
        if (isAppInForeground && sessionManager.isLoggedIn.value) {
            checkSessionExpiration()
        }
    }

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(this)
        Log.i(TAG, "SessionExpirationManager initialized with timeout of ${SESSION_TIMEOUT_DURATION / 1000} seconds")
    }
    
    /**
     * Sets a listener to be notified when the session expires
     */
    fun setSessionExpirationListener(listener: SessionExpirationListener) {
        this.sessionExpirationListener = listener
        Log.d(TAG, "Session expiration listener set")
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
                        sessionExpirationListener?.onSessionExpired()
                        // Reset background time
                        lastBackgroundTime = 0
                        isAppInForeground = true
                        return
                    }
                }
                
                // If session hasn't expired, start checking again
                resetInactivityTimer()
                scheduleSessionTimeout()
                lastBackgroundTime = 0
            } else {
                // App going to background
                lastBackgroundTime = System.currentTimeMillis()
                handler.removeCallbacks(sessionTimeoutRunnable)
            }
        }
        
        isAppInForeground = inForeground
    }

    /**
     * Resets the inactivity timer when user interacts with the app
     */
    fun resetInactivityTimer() {
        lastUserInteractionTime = System.currentTimeMillis()
        Log.v(TAG, "Inactivity timer reset")
    }

    /**
     * Check if the session has expired based on user inactivity
     */
    private fun checkSessionExpiration() {
        if (!isAppInForeground || !sessionManager.isLoggedIn.value) return
        
        val currentTime = System.currentTimeMillis()
        val inactiveTime = currentTime - lastUserInteractionTime
        
        if (inactiveTime >= SESSION_TIMEOUT_DURATION) {
            // Session expired, log out
            Log.i(TAG, "Session expired after ${inactiveTime / 1000} seconds of inactivity")
            sessionManager.clearSession()
            sessionExpirationListener?.onSessionExpired()
        } else {
            // Schedule next check
            val remainingTime = SESSION_TIMEOUT_DURATION - inactiveTime
            Log.d(TAG, "Session expiration check: ${inactiveTime / 1000} seconds inactive, ${remainingTime / 1000} seconds remaining")
            scheduleSessionTimeout()
        }
    }

    private fun scheduleSessionTimeout() {
        handler.removeCallbacks(sessionTimeoutRunnable)
        val currentTime = System.currentTimeMillis()
        val inactiveTime = currentTime - lastUserInteractionTime
        val remainingTime = SESSION_TIMEOUT_DURATION - inactiveTime
        
        // Schedule next check for either the remaining time or 10 seconds, whichever is smaller
        val checkInterval = minOf(remainingTime, 10000)
        if (checkInterval > 0) {
            handler.postDelayed(sessionTimeoutRunnable, checkInterval)
        } else {
            // If no time remaining, check immediately
            handler.post(sessionTimeoutRunnable)
        }
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
}