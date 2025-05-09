package com.androidghanem.oynxrestaurantdelivery.ui.screens.login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.data.repository.DeliveryRepositoryImpl
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.OnyxApplication
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
    val selectedLanguage: Language? = null,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val deliveryDriverName: String? = null
)

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {
    
    private val appInstance: OnyxApplication = application as OnyxApplication
    private val languageRepository: LanguageRepository = appInstance.languageRepository
    private val deliveryRepository: DeliveryRepository = DeliveryRepositoryImpl()
    private val sessionManager = appInstance.sessionManager
    
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
        val currentState = _uiState.value
        
        // Validate inputs
        if (currentState.userId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Delivery ID is required") }
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Password is required") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            deliveryRepository.login(
                deliveryId = currentState.userId,
                password = currentState.password,
                languageCode = currentState.selectedLanguage?.code ?: "1"
            ).onSuccess { driverInfo ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null,
                        deliveryDriverName = driverInfo.name
                    )
                }
                // Save session data
                sessionManager.saveSession(driverInfo)
            }.onFailure { exception ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}