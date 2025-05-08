package com.androidghanem.domain.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    private val SUPPORTED_LOCALES = arrayOf("en", "ar")
    private const val DEFAULT_LOCALE = "en"
    
    fun setLocale(context: Context, languageCode: String): Context {
        val validLanguageCode = if (languageCode in SUPPORTED_LOCALES) languageCode else DEFAULT_LOCALE
        val locale = Locale(validLanguageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }

}