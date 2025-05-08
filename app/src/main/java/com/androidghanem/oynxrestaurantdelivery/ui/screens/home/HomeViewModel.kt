package com.androidghanem.oynxrestaurantdelivery.ui.screens.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.model.Order
import com.androidghanem.domain.model.OrderStatus
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.OnyxApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OrderTab {
    NEW, OTHERS
}

data class HomeUiState(
    val isLanguageDialogVisible: Boolean = false,
    val availableLanguages: List<Language> = emptyList(),
    val selectedLanguage: Language? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val appInstance: OnyxApplication = application as OnyxApplication
    private val languageRepository: LanguageRepository = appInstance.languageRepository

    // Tab state
    private val _orderTabState = MutableStateFlow(OrderTab.NEW)
    val orderTabState: StateFlow<OrderTab> = _orderTabState.asStateFlow()

    // Orders state
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // UI state for language dialog
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    private val newOrders = listOf(
        Order("1569987", OrderStatus.NEW, "400 LE", "1/1/2020"),
        Order("1569988", OrderStatus.NEW, "400 LE", "1/1/2020"),
        Order("1569989", OrderStatus.NEW, "400 LE", "1/1/2020"),
        Order("1569990", OrderStatus.NEW, "400 LE", "1/1/2020"),
        Order("1569991", OrderStatus.NEW, "400 LE", "1/1/2020")
    )

    private val otherOrders = listOf(
        Order("1569991", OrderStatus.DELIVERING, "6378 LE", "11/6/2020"),
        Order("1569982", OrderStatus.RETURNED, "400 LE", "1/1/2020"),
        Order("1569993", OrderStatus.DELIVERED, "6378 LE", "11/6/2020"),
        Order("1569984", OrderStatus.RETURNED, "400 LE", "1/1/2020"),
        Order("1569995", OrderStatus.DELIVERED, "6378 LE", "11/6/2020"),
        Order("1569996", OrderStatus.DELIVERING, "6378 LE", "11/6/2020")
    )

    init {
        fetchOrders()
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

    fun updateOrderTab(tab: OrderTab) {
        if (_orderTabState.value != tab) {
            _orderTabState.value = tab
            // Show loading when switching tabs
            _isLoading.value = true
            fetchOrders()
        }
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                // Simulate network delay - increased to 2000ms (2 seconds)
                delay(2000)

                // Update orders based on selected tab
                _orders.value = when (_orderTabState.value) {
                    OrderTab.NEW -> newOrders
                    OrderTab.OTHERS -> otherOrders
                }
            } catch (_: Exception) {
                // Handle any potential exception
                _orders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}