package com.androidghanem.data.repository

import com.androidghanem.data.preferences.AppPreferencesManager
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.repository.LanguageRepository
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val preferencesManager: AppPreferencesManager
) : LanguageRepository {

    override fun getAvailableLanguages(callback: (List<Language>) -> Unit) {
        val currentCode = preferencesManager.getLanguageCode().lowercase()
        val languages = listOf(
            Language("ar", "العربية", isSelected = currentCode == "ar"),
            Language("en", "English", isSelected = currentCode == "en")
        )
        callback(languages)
    }

    override fun getSelectedLanguage(callback: (Language) -> Unit) {
        val code = preferencesManager.getLanguageCode()
        val language = when (code.lowercase()) {
            "ar" -> Language("ar", "العربية")
            else -> Language("en", "English")
        }
        callback(language)
    }

    override fun setSelectedLanguage(languageCode: String) {
        preferencesManager.setLanguageCode(languageCode)
    }
}