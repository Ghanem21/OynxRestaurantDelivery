package com.androidghanem.data.session

import android.content.Context
import android.content.SharedPreferences
import com.androidghanem.domain.model.DeliveryDriverInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.androidghanem.domain.session.SessionManager as DomainSessionManager

/**
 * Manages user session data, including login status and user information
 */
class SessionManager(context: Context) : DomainSessionManager {
    
    companion object {
        private const val PREFS_NAME = "onyx_delivery_prefs"
        private const val KEY_DRIVER_ID = "driver_id"
        private const val KEY_DRIVER_NAME = "driver_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _currentDriverInfo = MutableStateFlow<DeliveryDriverInfo?>(null)
    override val currentDriverInfo: StateFlow<DeliveryDriverInfo?> =
        _currentDriverInfo.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        // Load saved session data if any
        loadSessionData()
    }
    
    private fun loadSessionData() {
        val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        _isLoggedIn.value = isLoggedIn
        
        if (isLoggedIn) {
            val driverId = sharedPreferences.getString(KEY_DRIVER_ID, "") ?: ""
            val driverName = sharedPreferences.getString(KEY_DRIVER_NAME, "") ?: ""
            
            if (driverId.isNotEmpty()) {
                _currentDriverInfo.value = DeliveryDriverInfo(
                    deliveryId = driverId,
                    name = driverName
                )
            }
        }
    }
    
    /**
     * Saves the delivery driver session data and marks the user as logged in
     */
    override fun saveSession(driverInfo: DeliveryDriverInfo) {
        sharedPreferences.edit().apply {
            putString(KEY_DRIVER_ID, driverInfo.deliveryId)
            putString(KEY_DRIVER_NAME, driverInfo.name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
        
        _currentDriverInfo.value = driverInfo
        _isLoggedIn.value = true
    }
    
    /**
     * Clears the session data and marks the user as logged out
     */
    override fun clearSession() {
        sharedPreferences.edit().apply {
            remove(KEY_DRIVER_ID)
            remove(KEY_DRIVER_NAME)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
        
        _currentDriverInfo.value = null
        _isLoggedIn.value = false
    }
}