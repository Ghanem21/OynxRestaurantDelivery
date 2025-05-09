package com.androidghanem.data.network.model.request

/**
 * Request model for updating delivery bill status
 */
data class UpdateBillStatusRequest(
    val P_LANG_NO: String,
    val P_BILL_SRL: String,
    val P_DLVRY_STATUS_FLG: String,
    val P_DLVRY_RTRN_RSN: String
)