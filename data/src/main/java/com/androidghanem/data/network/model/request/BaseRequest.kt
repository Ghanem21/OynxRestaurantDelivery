package com.androidghanem.data.network.model.request

/**
 * Base request structure for OnyxDeliveryService API
 */
data class BaseRequest<T>(
    val Value: T
)