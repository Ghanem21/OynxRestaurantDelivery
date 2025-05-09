package com.androidghanem.data.network.model.response

/**
 * Base response structure for OnyxDeliveryService API
 */
data class BaseResponse<T>(
    val Data: T?,
    val Result: ResultStatus
)

/**
 * Result status part of API responses
 */
data class ResultStatus(
    val ErrNo: Int,
    val ErrMsg: String
)