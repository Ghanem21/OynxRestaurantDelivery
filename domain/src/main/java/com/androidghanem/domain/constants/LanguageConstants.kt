package com.androidghanem.domain.constants

/**
 * Constants for language codes used throughout the application.
 * Centralizes language code mapping between UI and API.
 */
object LanguageConstants {
    // UI language codes (ISO 639-1)
    const val ARABIC_UI = "ar"

    // API language codes
    const val ARABIC_API = "1"
    const val ENGLISH_API = "2"
    
    /**
     * Maps UI language codes to API language codes
     */
    fun mapUiToApiLanguage(uiLanguageCode: String): String {
        return when (uiLanguageCode) {
            ARABIC_UI -> ARABIC_API
            else -> ENGLISH_API
        }
    }

}