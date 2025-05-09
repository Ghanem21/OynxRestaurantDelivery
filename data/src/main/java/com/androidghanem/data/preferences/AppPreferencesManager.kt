package com.androidghanem.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppPreferencesManager(context: Context) {
    
    companion object {
        private const val PREFERENCES_NAME = "app_preferences"
        private const val SELECTED_LANGUAGE_CODE = "selected_language_code"
        private const val DEFAULT_LANGUAGE_CODE = "en"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    
    private val _languageCodeFlow = MutableStateFlow(getLanguageCode())
    val languageCode: StateFlow<String> = _languageCodeFlow
    
    fun getLanguageCode(): String {
        return prefs.getString(SELECTED_LANGUAGE_CODE, DEFAULT_LANGUAGE_CODE) ?: DEFAULT_LANGUAGE_CODE
    }
    
    fun setLanguageCode(languageCode: String) {
        prefs.edit { putString(SELECTED_LANGUAGE_CODE, languageCode) }
        _languageCodeFlow.value = languageCode
    }
}