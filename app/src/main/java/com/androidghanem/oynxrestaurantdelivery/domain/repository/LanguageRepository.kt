package com.androidghanem.oynxrestaurantdelivery.domain.repository

import com.androidghanem.oynxrestaurantdelivery.domain.model.Language

interface LanguageRepository {
    fun getAvailableLanguages(onResult: (List<Language>) -> Unit)
    fun setSelectedLanguage(languageCode: String)
    fun getSelectedLanguage(onResult: (Language) -> Unit)
}