package com.androidghanem.domain.repository

import com.androidghanem.domain.model.DeliveryBillItem
import com.androidghanem.domain.model.DeliveryDriverInfo

interface DeliveryRepository {
    suspend fun login(deliveryId: String, password: String, languageCode: String = "1"): Result<DeliveryDriverInfo>

    suspend fun getDeliveryBills(
        deliveryId: String,
        billSerial: String = "",
        processedFlag: String = "",
        languageCode: String = "1"
    ): Result<List<DeliveryBillItem>>

}