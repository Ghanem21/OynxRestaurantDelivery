package com.androidghanem.oynxrestaurantdelivery.ui.screens.login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.oynxrestaurantdelivery.data.preferences.AppPreferencesManager
import com.androidghanem.oynxrestaurantdelivery.data.repository.LanguageRepositoryImpl
import com.androidghanem.oynxrestaurantdelivery.domain.model.Language
import com.androidghanem.oynxrestaurantdelivery.domain.repository.LanguageRepository
import com.androidghanem.oynxrestaurantdelivery.utils.LocaleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val userId: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLanguageDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val availableLanguages: List<Language> = emptyList(),
    val selectedLanguage: Language? = null
)

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {
    
    private val preferencesManager = AppPreferencesManager(application)
    private val languageRepository: LanguageRepository = LanguageRepositoryImpl(preferencesManager)
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        loadLanguages()
    }
    
    private fun loadLanguages() {
        languageRepository.getAvailableLanguages { languages ->
            _uiState.update { it.copy(availableLanguages = languages) }
        }
        
        languageRepository.getSelectedLanguage { language ->
            _uiState.update { it.copy(selectedLanguage = language) }
        }
    }
    
    fun onUserIdChange(userId: String) {
        _uiState.update { it.copy(userId = userId) }
    }
    
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun toggleLanguageDialog() {
        _uiState.update { it.copy(isLanguageDialogVisible = !it.isLanguageDialogVisible) }
    }
    
    fun selectLanguage(languageCode: String) {
        val newLanguage = _uiState.value.availableLanguages.find { it.code == languageCode }
        newLanguage?.let {
            _uiState.update { state -> 
                state.copy(selectedLanguage = it) 
            }
        }
    }

    fun applyLanguageChange() {
        val selectedLanguage = _uiState.value.selectedLanguage
        selectedLanguage?.let {
            languageRepository.setSelectedLanguage(it.code)
            LocaleHelper.setLocale(getApplication(), it.code)
            preferencesManager.setLanguageCode(it.code)
            getApplication<Application>().startActivity(
                Intent.makeRestartActivityTask(
                    getApplication<Application>().packageManager.getLaunchIntentForPackage(
                        getApplication<Application>().packageName
                    )?.component
                )
            )
        }
        _uiState.update { it.copy(isLanguageDialogVisible = false) }
    }
    
    fun login() {
        _uiState.update { it.copy(isLoading = true) }
        // Implement actual login logic here
        // For demo, just simulate loading
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}