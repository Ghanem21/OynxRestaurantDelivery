package com.androidghanem.oynxrestaurantdelivery

import android.app.Application
import android.content.Context
import com.androidghanem.data.preferences.AppPreferencesManager
import com.androidghanem.data.repository.LanguageRepositoryImpl
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper

class OnyxApplication : Application() {
    
    lateinit var preferencesManager: AppPreferencesManager
        private set
    
    lateinit var languageRepository: LanguageRepository
        private set
    
    override fun attachBaseContext(base: Context) {
        preferencesManager = AppPreferencesManager(base)
        val languageCode = preferencesManager.getLanguageCode()
        super.attachBaseContext(LocaleHelper.setLocale(base, languageCode))
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize repositories
        languageRepository = LanguageRepositoryImpl(preferencesManager)
    }
    

}