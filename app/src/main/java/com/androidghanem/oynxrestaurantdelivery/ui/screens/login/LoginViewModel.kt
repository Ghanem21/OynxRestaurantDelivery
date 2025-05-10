package com.androidghanem.oynxrestaurantdelivery.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val languageRepository: LanguageRepository,
    private val deliveryRepository: DeliveryRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())

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

    fun toggleLanguageDialog() {
        _uiState.update { it.copy(isLanguageDialogVisible = !it.isLanguageDialogVisible) }
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

}