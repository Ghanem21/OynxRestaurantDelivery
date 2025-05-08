package com.androidghanem.domain.repository

import com.androidghanem.domain.model.Language

interface LanguageRepository {
    fun getAvailableLanguages(onResult: (List<Language>) -> Unit)
    fun setSelectedLanguage(languageCode: String)
    fun getSelectedLanguage(onResult: (Language) -> Unit)
}