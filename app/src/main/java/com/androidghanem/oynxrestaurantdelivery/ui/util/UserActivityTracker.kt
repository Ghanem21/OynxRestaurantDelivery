package com.androidghanem.oynxrestaurantdelivery.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
    private val TAG = "UserActivityTracker"
    private var isInitialized = false

    /**
     * Initialize activity tracking for an Activity
     */
    @SuppressLint("ClickableViewAccessibility")
    fun initialize(activity: Activity) {
        if (isInitialized) {
            Log.d(TAG, "UserActivityTracker already initialized")
            return
        }

        // Set touch listener on the root view
        activity.window.decorView.setOnTouchListener { _, event -> 
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                Log.v(TAG, "Touch event detected, resetting inactivity timer")
                sessionExpirationManager.resetInactivityTimer()
            }
            false
        }

        // Find all focusable views and set focus change listeners
        findAllFocusableViews(activity.window.decorView as ViewGroup)?.forEach { view ->
            view.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Log.v(TAG, "Focus event detected, resetting inactivity timer")
                    sessionExpirationManager.resetInactivityTimer()
                }
            }
        }

        isInitialized = true
        Log.d(TAG, "UserActivityTracker initialized for ${activity.javaClass.simpleName}")
    }
    
    /**
     * Call this method when user interaction is detected
     */
    fun onUserInteraction() {
        Log.v(TAG, "User interaction detected, resetting inactivity timer")
        sessionExpirationManager.resetInactivityTimer()
    }

    /**
     * Find all focusable views in the view hierarchy
     */
    private fun findAllFocusableViews(viewGroup: ViewGroup): List<View>? {
        val views = mutableListOf<View>()

        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child.isFocusable) {
                views.add(child)
            }

            if (child is ViewGroup) {
                views.addAll(findAllFocusableViews(child) ?: emptyList())
            }
        }

        return views
    }
}