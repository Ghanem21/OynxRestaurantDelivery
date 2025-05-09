package com.androidghanem.data.network.model.response

data class BaseResponse<T>(
    val Data: T?,
    val Result: ResultStatus
)

data class ResultStatus(
    val ErrNo: Int,
    val ErrMsg: String
)