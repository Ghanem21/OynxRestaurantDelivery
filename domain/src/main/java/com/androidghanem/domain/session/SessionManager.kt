package com.androidghanem.domain.session

import com.androidghanem.domain.model.DeliveryDriverInfo
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing user session data
 */
interface SessionManager {
    /**
     * Current driver information state flow
     */
    val currentDriverInfo: StateFlow<DeliveryDriverInfo?>

    /**
     * Login status state flow
     */
    val isLoggedIn: StateFlow<Boolean>

    /**
     * Saves the delivery driver session data and marks the user as logged in
     */
    fun saveSession(driverInfo: DeliveryDriverInfo)

    /**
     * Clears the session data and marks the user as logged out
     */
    fun clearSession()
}