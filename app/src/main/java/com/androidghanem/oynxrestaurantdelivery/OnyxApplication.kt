package com.androidghanem.oynxrestaurantdelivery

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.androidghanem.data.preferences.AppPreferencesManager
import com.androidghanem.data.session.SessionExpirationManager
import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.ui.navigation.SessionExpirationHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OnyxApplication : Application(), Application.ActivityLifecycleCallbacks {
    
    companion object {
        private const val TAG = "SessionExpiration"
    }
    
    @Inject
    lateinit var preferencesManager: AppPreferencesManager
    
    @Inject
    lateinit var languageRepository: LanguageRepository
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    @Inject
    lateinit var sessionExpirationManager: SessionExpirationManager
    
    @Inject
    lateinit var deliveryRepository: DeliveryRepository
    
    override fun attachBaseContext(base: Context) {
        val tempPreferences = AppPreferencesManager(base)
        val languageCode = tempPreferences.getLanguageCode()
        super.attachBaseContext(LocaleHelper.setLocale(base, languageCode))
    }
    
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Initializing session expiration manager with 2-minute timeout")

        // Register session expiration listener
        sessionExpirationManager.setSessionExpirationListener(object : SessionExpirationManager.Companion.SessionExpirationListener {
            override fun onSessionExpired() {
                Log.i(TAG, "Session expired callback triggered in OnyxApplication")
                // Clear any cached data that requires authentication
                try {
                    // Attempt to clear repository cache if method exists
                    deliveryRepository.javaClass.getMethod("clearCache").invoke(deliveryRepository)
                } catch (e: Exception) {
                    Log.d(TAG, "No clearCache method found in repository")
                }
                // Notify the SessionExpirationHandler to navigate to login screen
                SessionExpirationHandler.sessionExpired()
            }
        })

        // Register this application as a lifecycle callback listener
        registerActivityLifecycleCallbacks(this)

        Log.d(TAG, "Session expiration monitoring initialized using coroutines")
    }

    override fun onTerminate() {
        super.onTerminate()
        if (::sessionExpirationManager.isInitialized) {
            Log.d(TAG, "Application terminating, shutting down session manager")
            sessionExpirationManager.shutdown()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "Activity started: ${activity.javaClass.simpleName}")
        resetSessionTimer()
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "Activity resumed: ${activity.javaClass.simpleName}")
        resetSessionTimer()
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "Activity stopped: ${activity.javaClass.simpleName}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    fun resetSessionTimer() {
        if (::sessionExpirationManager.isInitialized) {
            Log.v(TAG, "Resetting session timer")
            sessionExpirationManager.resetInactivityTimer()
        } else {
            Log.w(TAG, "Session expiration manager not initialized yet!")
        }
    }
}