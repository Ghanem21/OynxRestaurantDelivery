package com.androidghanem.data.network.model.response

import com.androidghanem.domain.model.DeliveryBillItem

data class DeliveryBillsWrapper(
    val DeliveryBills: List<DeliveryBillResponse>? = null
)

data class DeliveryBillResponse(
    val BILL_TYPE: String,
    val BILL_NO: String,
    val BILL_SRL: String,
    val BILL_DATE: String,
    val BILL_TIME: String,
    val BILL_AMT: String,
    val TAX_AMT: String,
    val DLVRY_AMT: String,
    val MOBILE_NO: String,
    val CSTMR_NM: String,
    val RGN_NM: String,
    val CSTMR_BUILD_NO: String,
    val CSTMR_FLOOR_NO: String,
    val CSTMR_APRTMNT_NO: String,
    val CSTMR_ADDRSS: String,
    val LATITUDE: String,
    val LONGITUDE: String,
    val DLVRY_STATUS_FLG: String
) {
    fun toDomain(): DeliveryBillItem {
        val totalAmount = try {
            BILL_AMT.toDouble()
        } catch (_: Exception) {
            0.0
        }
        
        return DeliveryBillItem(
            billSerial = BILL_SRL,
            billNumber = BILL_NO,
            billDate = BILL_DATE,
            customerName = CSTMR_NM,
            totalAmount = totalAmount,
            statusFlag = DLVRY_STATUS_FLG,
            statusDescription = "",
            region = RGN_NM,
            address = CSTMR_ADDRSS,
            mobileNumber = MOBILE_NO,
            deliveryAmount = DLVRY_AMT
        )
    }
}