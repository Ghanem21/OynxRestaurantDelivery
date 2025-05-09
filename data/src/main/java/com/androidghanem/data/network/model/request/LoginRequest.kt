package com.androidghanem.data.network.model.request

/**
 * Request model for delivery login
 */
data class LoginRequest(
    val P_LANG_NO: String,
    val P_DLVRY_NO: String,
    val P_PSSWRD: String
)