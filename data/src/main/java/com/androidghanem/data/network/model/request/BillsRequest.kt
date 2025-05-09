package com.androidghanem.data.network.model.request

/**
 * Request model for fetching delivery bills
 */
data class BillsRequest(
    val P_DLVRY_NO: String,
    val P_LANG_NO: String,
    val P_BILL_SRL: String,
    val P_PRCSSD_FLG: String
)