package com.androidghanem.domain.repository

import com.androidghanem.domain.model.DeliveryDriverInfo

/**
 * Repository interface for delivery operations
 */
interface DeliveryRepository {
    /**
     * Validates driver login credentials
     *
     * @param deliveryId The delivery driver ID
     * @param password The delivery driver password
     * @param languageCode The language code (default is "1")
     * @return Result containing delivery information on success
     */
    suspend fun login(deliveryId: String, password: String, languageCode: String = "1"): Result<DeliveryDriverInfo>

    /**
     * Changes delivery driver password
     *
     * @param deliveryId The delivery driver ID
     * @param oldPassword The current password
     * @param newPassword The new password
     * @param languageCode The language code (default is "1")
     * @return Result indicating success or failure
     */
    suspend fun changePassword(
        deliveryId: String,
        oldPassword: String,
        newPassword: String,
        languageCode: String = "1"
    ): Result<Boolean>
    
    /**
     * Gets delivery bills for a driver
     *
     * @param deliveryId The delivery driver ID
     * @param billSerial Optional bill serial number filter
     * @param processedFlag Optional processed flag filter
     * @param languageCode The language code (default is "1")
     * @return Result containing the list of bills
     */
    suspend fun getDeliveryBills(
        deliveryId: String,
        billSerial: String = "",
        processedFlag: String = "",
        languageCode: String = "1"
    ): Result<List<Any>>
    
    /**
     * Gets delivery status types
     *
     * @param languageCode The language code (default is "1")
     * @return Result containing the list of status types
     */
    suspend fun getDeliveryStatusTypes(languageCode: String = "1"): Result<List<Any>>
    
    /**
     * Gets return bill reasons
     *
     * @param languageCode The language code (default is "1")
     * @return Result containing the list of reasons
     */
    suspend fun getReturnBillReasons(languageCode: String = "1"): Result<List<Any>>
    
    /**
     * Updates delivery bill status
     *
     * @param billSerial The bill serial number
     * @param statusFlag The new status flag
     * @param returnReason Optional return reason
     * @param languageCode The language code (default is "1")
     * @return Result indicating success or failure
     */
    suspend fun updateDeliveryBillStatus(
        billSerial: String,
        statusFlag: String,
        returnReason: String = "",
        languageCode: String = "1"
    ): Result<Boolean>
}