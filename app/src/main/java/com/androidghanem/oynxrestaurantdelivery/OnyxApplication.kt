package com.androidghanem.oynxrestaurantdelivery

import android.app.Application
import android.content.Context
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
class OnyxApplication : Application() {
    
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
        
        sessionExpirationManager.setSessionExpirationListener(object : SessionExpirationManager.Companion.SessionExpirationListener {
            override fun onSessionExpired() {
                Log.i(TAG, "Session expired callback triggered in OnyxApplication")
                SessionExpirationHandler.sessionExpired()
            }
        })
    }
    
    fun resetSessionTimer() {
        if (::sessionExpirationManager.isInitialized) {
            sessionExpirationManager.resetInactivityTimer()
        }
    }
}