package com.androidghanem.oynxrestaurantdelivery.ui.screens.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidghanem.data.repository.DeliveryRepositoryCachedImpl
import com.androidghanem.domain.model.Language
import com.androidghanem.domain.model.Order
import com.androidghanem.domain.model.OrderStatus
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.repository.LanguageRepository
import com.androidghanem.domain.utils.LocaleHelper
import com.androidghanem.oynxrestaurantdelivery.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OrderTab {
    NEW, OTHERS;
    
    fun getDisplayText(context: Context): String {
        return when (this) {
            NEW -> context.getString(R.string.tab_new)
            OTHERS -> context.getString(R.string.tab_others)
        }
    }

}

data class HomeUiState(
    val isLanguageDialogVisible: Boolean = false,
    val availableLanguages: List<Language> = emptyList(),
    val selectedLanguage: Language? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val languageRepository: LanguageRepository,
    private val sessionManager: com.androidghanem.data.session.SessionManager, 
    private val deliveryRepository: DeliveryRepository
) : ViewModel() {
    // Tab state
    private val _orderTabState = MutableStateFlow(OrderTab.NEW)
    val orderTabState: StateFlow<OrderTab> = _orderTabState.asStateFlow()

    // Orders state
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _errorState = MutableStateFlow<String?>(null)

    // Offline mode flag
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    // UI state for language dialog
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Driver info state
    private val _driverName = MutableStateFlow("")
    val driverName: StateFlow<String> = _driverName.asStateFlow()

    // Driver ID for API calls
    private var driverId: String = ""
    
    // Flag to check if current language is Arabic
    private val _isArabic = MutableStateFlow(false)

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
            _isArabic.value = language.code == "ar"
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
            LocaleHelper.setLocale(context, it.code)
            _isArabic.value = it.code == "ar"
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
                // Reset error state and offline mode
                _errorState.value = null
                _isOfflineMode.value = false

                // Get the language code for API
                val languageCode = getLanguageCodeForApi()

                if (driverId.isNotEmpty()) {

                    // Call the API
                    val result = deliveryRepository.getDeliveryBills(
                        deliveryId = driverId,
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
                            // When API fails, load from cache
                            fetchOrdersFromCache()
                            _errorState.value = error.message
                            _isOfflineMode.value = true
                            Log.e("HomeViewModel", "Error loading orders: ${error.message}", error)
                        }
                    )
                } else {
                    // No driver ID, show empty state
                    _orders.value = emptyList()
                    Log.w("HomeViewModel", "No driver ID available")
                }
            } catch (e: Exception) {
                // Handle any potential exception by loading from cache
                fetchOrdersFromCache()
                _errorState.value = e.message
                _isOfflineMode.value = true
                Log.e("HomeViewModel", "Exception while loading orders", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetches orders from the local database cache
     */
    private fun fetchOrdersFromCache() {
        viewModelScope.launch {
            try {
                if (driverId.isNotEmpty()) {
                    // Cast to cached implementation to access cache methods
                    val cachedRepo = deliveryRepository as? DeliveryRepositoryCachedImpl
                    
                    if (cachedRepo != null) {
                        when (_orderTabState.value) {
                            OrderTab.NEW -> {
                                cachedRepo.getNewOrdersFromCache(driverId).collect { cachedOrders ->
                                    _orders.value = cachedOrders
                                    Log.d("HomeViewModel", "Loaded ${cachedOrders.size} orders from cache for tab NEW")
                                }
                            }
                            OrderTab.OTHERS -> {
                                cachedRepo.getProcessedOrdersFromCache(driverId).collect { cachedOrders ->
                                    _orders.value = cachedOrders
                                    Log.d("HomeViewModel", "Loaded ${cachedOrders.size} orders from cache for tab OTHERS")
                                }
                            }
                        }
                    } else {
                        Log.e("HomeViewModel", "Repository is not a cached implementation")
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading from cache", e)
                _orders.value = emptyList()
            }
        }
    }

    /**
     * Gets the language code for API calls
     * 1 for Arabic, 2 for anything else
     */
    private fun getLanguageCodeForApi(): String {
        val selectedLanguage = _uiState.value.selectedLanguage?.code ?: "en"
        return if (selectedLanguage == "ar") "1" else "2"
    }
}