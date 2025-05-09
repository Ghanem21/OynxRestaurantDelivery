package com.androidghanem.oynxrestaurantdelivery.ui.util

import android.view.MotionEvent
import android.view.View
import com.androidghanem.oynxrestaurantdelivery.OnyxApplication

/**
 * A utility class to detect user touch interactions and reset the session timer
 */
class UserActivityDetector(private val application: OnyxApplication) : View.OnTouchListener {
    
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // Reset session timer on any touch event
        if (event.action == MotionEvent.ACTION_DOWN || 
            event.action == MotionEvent.ACTION_MOVE) {
            application.resetSessionTimer()
        }
        
        // Return false to allow the touch event to be handled by other listeners
        return false
    }
    
    companion object {
        /**
         * Applies the activity detector to a view
         */
        fun applyTo(view: View) {
            val application = view.context.applicationContext as? OnyxApplication ?: return
            val detector = UserActivityDetector(application)
            view.setOnTouchListener(detector)
        }
    }
}