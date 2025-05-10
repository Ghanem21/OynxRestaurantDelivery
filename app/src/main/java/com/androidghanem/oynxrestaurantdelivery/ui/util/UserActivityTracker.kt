package com.androidghanem.oynxrestaurantdelivery.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MotionEvent
import com.androidghanem.data.session.SessionExpirationManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks user activity to reset session expiration timer
 */
@Singleton
class UserActivityTracker @Inject constructor(
    private val sessionExpirationManager: SessionExpirationManager
) {
    /**
     * Initialize activity tracking for an Activity
     */
    @SuppressLint("ClickableViewAccessibility")
    fun initialize(activity: Activity) {
        activity.window.decorView.setOnTouchListener { _, event -> 
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                sessionExpirationManager.resetInactivityTimer()
            }
            false
        }
    }
    
    /**
     * Call this method when user interaction is detected
     */
    fun onUserInteraction() {
        sessionExpirationManager.resetInactivityTimer()
    }
}