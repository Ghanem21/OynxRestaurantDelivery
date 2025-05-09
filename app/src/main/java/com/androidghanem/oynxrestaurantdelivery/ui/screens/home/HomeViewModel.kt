package com.androidghanem.oynxrestaurantdelivery.ui.screens.home

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.data.repository.LanguageRepositoryImpl
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.model.Order
import com.androidghanem.domain.model.OrderStatus
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.OnyxApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OrderTab {
    NEW, OTHERS;
    
    fun getDisplayText(): String {
        return when (this) {
            NEW -> "New"
            OTHERS -> "Orders"
        }
    }
}

data class HomeUiState(
    val isLanguageDialogVisible: Boolean = false,
    val availableLanguages: List<Language> = emptyList(),
    val selectedLanguage: Language? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val appInstance: OnyxApplication = application as OnyxApplication
    private val languageRepository: LanguageRepository = appInstance.languageRepository
    private val sessionManager = appInstance.sessionManager
    private val deliveryRepository: DeliveryRepository = appInstance.deliveryRepository

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

    // Driver info state
    private val _driverName = MutableStateFlow("")
    val driverName: StateFlow<String> = _driverName.asStateFlow()

    // Driver ID for API calls
    private var driverId: String = ""

    init {
        fetchOrders()
        loadLanguages()
        loadDriverInfo()
        loadStatusTypes()
    }

    private fun loadLanguages() {
        languageRepository.getAvailableLanguages { languages ->
            _uiState.update { it.copy(availableLanguages = languages) }
        }

        languageRepository.getSelectedLanguage { language ->
            _uiState.update { it.copy(selectedLanguage = language) }
        }
    }

    private fun loadDriverInfo() {
        viewModelScope.launch {
            sessionManager.currentDriverInfo.collect { driverInfo ->
                _driverName.value = driverInfo?.name ?: ""
                driverId = driverInfo?.deliveryId ?: ""
                if (driverId.isNotEmpty()) {
                    fetchOrders()
                }
            }
        }
    }

    private fun loadStatusTypes() {
        viewModelScope.launch {
            try {
                val languageCode = getLanguageCodeForApi()
                deliveryRepository.getDeliveryStatusTypes(languageCode).fold(
                    onSuccess = { statusTypes ->
                        // Log status types for debugging
                        Log.d("HomeViewModel", "Status Types: $statusTypes")
                    },
                    onFailure = { error ->
                        Log.e("HomeViewModel", "Error loading status types: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception loading status types", e)
            }
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
                // Show loading
                _isLoading.value = true

                // Get the language code for API
                val languageCode = getLanguageCodeForApi()

                if (driverId.isNotEmpty()) {
                    // Determine what flag to use for status filtering based on selected tab
                    // "0" = NEW, "1" = DELIVERED, "2" = PARTIAL_RETURN, "3" = RETURNED
                    val processedFlag = if (_orderTabState.value == OrderTab.NEW) "0" else "1,2,3"

                    // Call the API
                    val result = deliveryRepository.getDeliveryBills(
                        deliveryId = driverId,
                        processedFlag = processedFlag,
                        languageCode = languageCode
                    )

                    result.fold(
                        onSuccess = { bills ->
                            // Map API responses to Order objects
                            val ordersList = bills.map { it.toOrder() }
                            
                            // Apply additional filtering in case the API doesn't filter properly
                            val filteredOrders = when (_orderTabState.value) {
                                OrderTab.NEW -> ordersList.filter { it.status == OrderStatus.NEW }
                                OrderTab.OTHERS -> ordersList.filter { it.status != OrderStatus.NEW }
                            }
                            
                            _orders.value = filteredOrders
                            
                            if (_orders.value.isEmpty()) {
                                Log.d("HomeViewModel", "No orders found for selected tab: ${_orderTabState.value}")
                            } else {
                                Log.d("HomeViewModel", "Loaded ${_orders.value.size} orders for tab: ${_orderTabState.value}")
                            }
                        },
                        onFailure = { error ->
                            _orders.value = emptyList()
                            Log.e("HomeViewModel", "Error loading orders: ${error.message}", error)
                        }
                    )
                } else {
                    // No driver ID, show empty state
                    _orders.value = emptyList()
                    Log.w("HomeViewModel", "No driver ID available")
                }
            } catch (e: Exception) {
                // Handle any potential exception
                _orders.value = emptyList()
                Log.e("HomeViewModel", "Exception while loading orders", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Gets the language code for API calls
     * 1 for Arabic, 2 for anything else
     */
    private fun getLanguageCodeForApi(): String {
        val selectedLanguage = _uiState.value.selectedLanguage?.code ?: "en"
        return LanguageRepositoryImpl.mapLanguageCodeToApi(selectedLanguage)
    }
}