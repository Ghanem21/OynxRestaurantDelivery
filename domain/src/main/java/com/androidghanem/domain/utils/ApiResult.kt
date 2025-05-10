package com.androidghanem.domain.utils

/**
 * A sealed class representing the result of an API operation.
 * Enforces proper error handling and provides a standard way to process API responses.
 */
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    sealed class Error : ApiResult<Nothing>() {
        data class NetworkError(val exception: Exception, val message: String = exception.message ?: "Network error") : Error()
        data class ServerError(val code: Int, val message: String) : Error()
        data class ValidationError(val field: String? = null, val message: String) : Error()
        data class UnknownError(val exception: Throwable? = null, val message: String = exception?.message ?: "Unknown error") : Error()
        
        val errorMessage: String
            get() = when (this) {
                is NetworkError -> message
                is ServerError -> message
                is ValidationError -> message
                is UnknownError -> message
            }
    }

}