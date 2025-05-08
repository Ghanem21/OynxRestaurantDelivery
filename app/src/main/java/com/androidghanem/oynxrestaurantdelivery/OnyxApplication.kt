package com.androidghanem.oynxrestaurantdelivery

import android.app.Application
import android.content.Context
import com.androidghanem.oynxrestaurantdelivery.data.preferences.AppPreferencesManager
import com.androidghanem.oynxrestaurantdelivery.utils.LocaleHelper

class OnyxApplication : Application() {
    
    private lateinit var preferencesManager: AppPreferencesManager
    
    override fun attachBaseContext(base: Context) {
        preferencesManager = AppPreferencesManager(base)
        val languageCode = preferencesManager.getLanguageCode()
        super.attachBaseContext(LocaleHelper.setLocale(base, languageCode))
    }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any other app-wide configurations here
    }
}