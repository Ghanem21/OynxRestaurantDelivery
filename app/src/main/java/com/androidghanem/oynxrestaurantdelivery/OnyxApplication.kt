package com.androidghanem.oynxrestaurantdelivery

import android.app.Application
import android.content.Context
import android.util.Log
import com.androidghanem.data.preferences.AppPreferencesManager
import com.androidghanem.data.repository.LanguageRepositoryImpl
import com.androidghanem.data.session.SessionExpirationManager
import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.ui.navigation.SessionExpirationHandler

class OnyxApplication : Application() {
    
    companion object {
        private const val TAG = "SessionExpiration"
    }
    
    lateinit var preferencesManager: AppPreferencesManager
        private set
    
    lateinit var languageRepository: LanguageRepository
        private set
    
    lateinit var sessionManager: SessionManager
        private set
        
    lateinit var sessionExpirationManager: SessionExpirationManager
        private set
    
    override fun attachBaseContext(base: Context) {
        preferencesManager = AppPreferencesManager(base)
        val languageCode = preferencesManager.getLanguageCode()
        super.attachBaseContext(LocaleHelper.setLocale(base, languageCode))
    }
    
    override fun onCreate() {
        super.onCreate()
        
        languageRepository = LanguageRepositoryImpl(preferencesManager)
        sessionManager = SessionManager(this)
        
        sessionExpirationManager = SessionExpirationManager(
            application = this,
            sessionManager = sessionManager
        )
        
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