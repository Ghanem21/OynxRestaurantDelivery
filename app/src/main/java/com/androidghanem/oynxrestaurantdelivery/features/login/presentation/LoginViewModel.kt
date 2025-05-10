package com.androidghanem.oynxrestaurantdelivery.features.login.presentation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.ApiResult
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.features.login.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Login screen state
 */
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

/**
 * Types of errors that can occur during login
 */
enum class ErrorType {
    NONE,
    NETWORK,
    VALIDATION,
    SERVER,
    UNKNOWN
}

/**
 * ViewModel for the login screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginUseCase: LoginUseCase,
    private val languageRepository: LanguageRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        loadLanguages()
    }
    
    /**
     * Loads available languages and the currently selected language
     */
    private fun loadLanguages() {
        languageRepository.getAvailableLanguages { languages ->
            _uiState.update { it.copy(availableLanguages = languages) }
        }
        
        languageRepository.getSelectedLanguage { language ->
            _uiState.update { it.copy(selectedLanguage = language) }
        }
    }
    
    /**
     * Updates the user ID field
     */
    fun onUserIdChange(userId: String) {
        _uiState.update { it.copy(userId = userId) }
    }
    
    /**
     * Updates the password field
     */
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    /**
     * Toggles the language selection dialog
     */
    fun toggleLanguageDialog() {
        _uiState.update { it.copy(isLanguageDialogVisible = !it.isLanguageDialogVisible) }
    }
    
    /**
     * Toggles password visibility
     */
    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }
    
    /**
     * Selects a language by code
     */
    fun selectLanguage(languageCode: String) {
        val newLanguage = _uiState.value.availableLanguages.find { it.code == languageCode }
        newLanguage?.let {
            _uiState.update { state -> 
                state.copy(selectedLanguage = it) 
            }
        }
    }
    
    /**
     * Selects and applies a language change in one step
     */
    fun selectAndApplyLanguage(languageCode: String) {
        selectLanguage(languageCode)
        applyLanguageChange()
        toggleLanguageDialog()
    }

    /**
     * Applies the selected language change
     */
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
    
    /**
     * Performs login authentication
     */
    fun login() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null, errorType = ErrorType.NONE) }
        
        viewModelScope.launch {
            val result = loginUseCase.execute(
                deliveryId = currentState.userId,
                password = currentState.password,
                languageCode = currentState.selectedLanguage?.code ?: "en"
            )
            
            when (result) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            errorMessage = null,
                            deliveryDriverName = result.data.name
                        )
                    }
                }
                is ApiResult.Error -> {
                    val errorType = when (result) {
                        is ApiResult.Error.NetworkError -> ErrorType.NETWORK
                        is ApiResult.Error.ServerError -> ErrorType.SERVER
                        is ApiResult.Error.ValidationError -> ErrorType.VALIDATION
                        is ApiResult.Error.UnknownError -> ErrorType.UNKNOWN
                    }
                    
                    val errorTitle = when (errorType) {
                        ErrorType.NETWORK -> "Network Error"
                        ErrorType.SERVER -> "Server Error"
                        ErrorType.VALIDATION -> "Validation Error"
                        ErrorType.UNKNOWN -> "Login Failed"
                        ErrorType.NONE -> null
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.errorMessage,
                            errorType = errorType,
                            errorMessageTitle = errorTitle
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Clears any error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, errorType = ErrorType.NONE, errorMessageTitle = null) }
    }
}