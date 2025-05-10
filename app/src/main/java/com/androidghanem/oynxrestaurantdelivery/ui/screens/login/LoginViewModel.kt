package com.androidghanem.oynxrestaurantdelivery.ui.screens.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    val deliveryDriverName: String? = null,
    val errorMessageTitle: String? = null,
    val errorType: ErrorType = ErrorType.NONE
)

enum class ErrorType {
    NONE,
    NETWORK,
    VALIDATION,
    SERVER,
    UNKNOWN
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val languageRepository: LanguageRepository,
    private val deliveryRepository: DeliveryRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
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
    
    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }
    
    fun selectLanguage(languageCode: String) {
        val newLanguage = _uiState.value.availableLanguages.find { it.code == languageCode }
        newLanguage?.let {
            _uiState.update { state -> 
                state.copy(selectedLanguage = it) 
            }
        }
    }
    
    fun selectAndApplyLanguage(languageCode: String) {
        selectLanguage(languageCode)
        applyLanguageChange()
        toggleLanguageDialog()
    }

    fun applyLanguageChange() {
        val selectedLanguage = _uiState.value.selectedLanguage
        selectedLanguage?.let {
            languageRepository.setSelectedLanguage(it.code)
            LocaleHelper.setLocale(context, it.code)
            context.startActivity(
                Intent.makeRestartActivityTask(
                    context.packageManager.getLaunchIntentForPackage(
                        context.packageName
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
            _uiState.update { it.copy(
                errorMessage = "Delivery ID is required",
                errorType = ErrorType.VALIDATION,
                errorMessageTitle = "Validation Error"
            ) }
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(
                errorMessage = "Password is required",
                errorType = ErrorType.VALIDATION,
                errorMessageTitle = "Validation Error"
            ) }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null, errorType = ErrorType.NONE) }
        
        viewModelScope.launch {
            deliveryRepository.login(
                deliveryId = currentState.userId,
                password = currentState.password,
                languageCode = currentState.selectedLanguage?.code ?: "en"
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
                val errorType = when {
                    exception.message?.contains("network", ignoreCase = true) == true -> ErrorType.NETWORK
                    exception.message?.contains("server", ignoreCase = true) == true -> ErrorType.SERVER
                    else -> ErrorType.UNKNOWN
                }
                
                val errorTitle = when (errorType) {
                    ErrorType.NETWORK -> "Network Error"
                    ErrorType.SERVER -> "Server Error"
                    else -> "Login Failed"
                }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed",
                        errorType = errorType,
                        errorMessageTitle = errorTitle
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, errorType = ErrorType.NONE, errorMessageTitle = null) }
    }
}