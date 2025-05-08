package com.androidghanem.data.repository

import com.androidghanem.data.preferences.AppPreferencesManager
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.repository.LanguageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LanguageRepositoryImpl(
    private val preferencesManager: AppPreferencesManager
) : LanguageRepository {
    private val availableLanguages = listOf(
        Language("ar", "Arabic", "العربية"),
        Language("en", "English", "English")
    )
    
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun getAvailableLanguages(onResult: (List<Language>) -> Unit) {
        val languageCode = preferencesManager.getLanguageCode()
        val languages = availableLanguages.map { language ->
            language.copy(isSelected = language.code == languageCode)
        }
        onResult(languages)
        
        // Listen for changes
        coroutineScope.launch {
            preferencesManager.languageCode.collect { selectedCode ->
                val updatedLanguages = availableLanguages.map { language ->
                    language.copy(isSelected = language.code == selectedCode)
                }
                onResult(updatedLanguages)
            }
        }
    }
    
    override fun setSelectedLanguage(languageCode: String) {
        preferencesManager.setLanguageCode(languageCode)
    }
    
    override fun getSelectedLanguage(onResult: (Language) -> Unit) {
        val languageCode = preferencesManager.getLanguageCode()
        val language = availableLanguages.find { it.code == languageCode } 
            ?: availableLanguages.find { it.code == "en" }
            ?: availableLanguages.first()
        onResult(language)
        
        // Listen for changes
        coroutineScope.launch {
            preferencesManager.languageCode.collect { selectedCode ->
                val updatedLanguage = availableLanguages.find { it.code == selectedCode }
                    ?: availableLanguages.find { it.code == "en" }
                    ?: availableLanguages.first()
                onResult(updatedLanguage)
            }
        }
    }
}