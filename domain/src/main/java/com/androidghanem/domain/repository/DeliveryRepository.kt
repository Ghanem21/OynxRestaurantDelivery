package com.androidghanem.domain.repository

import com.androidghanem.domain.model.DeliveryBillItem
import com.androidghanem.domain.model.DeliveryDriverInfo
import com.androidghanem.domain.model.DeliveryStatusType

interface DeliveryRepository {
    suspend fun login(deliveryId: String, password: String, languageCode: String = "1"): Result<DeliveryDriverInfo>

    suspend fun changePassword(
        deliveryId: String,
        oldPassword: String,
        newPassword: String,
        languageCode: String = "1"
    ): Result<Boolean>
    
    suspend fun getDeliveryBills(
        deliveryId: String,
        billSerial: String = "",
        processedFlag: String = "",
        languageCode: String = "1"
    ): Result<List<DeliveryBillItem>>
    
    suspend fun getDeliveryStatusTypes(languageCode: String = "1"): Result<List<DeliveryStatusType>>
    
    suspend fun updateDeliveryBillStatus(
        billSerial: String,
        statusFlag: String,
        returnReason: String = "",
        languageCode: String = "1"
    ): Result<Boolean>
}