package com.androidghanem.data.network.model.request

/**
 * Request model for changing delivery password
 */
data class ChangePasswordRequest(
    val P_LANG_NO: String,
    val P_DLVRY_NO: String,
    val P_OLD_PSSWRD: String,
    val P_NEW_PSSWRD: String
)