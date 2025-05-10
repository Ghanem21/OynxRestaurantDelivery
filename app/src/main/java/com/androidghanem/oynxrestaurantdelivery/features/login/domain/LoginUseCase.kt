package com.androidghanem.oynxrestaurantdelivery.features.login.domain

import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.model.DeliveryDriverInfo
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.utils.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase that handles login authentication and session management
 */
class LoginUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Execute the login authentication flow
     * 
     * @param deliveryId The driver's ID
     * @param password The driver's password
     * @param languageCode The current language code
     * @return ApiResult with driver info or error
     */
    suspend fun execute(
        deliveryId: String, 
        password: String, 
        languageCode: String
    ): ApiResult<DeliveryDriverInfo> = withContext(Dispatchers.IO) {
        if (deliveryId.isBlank()) {
            return@withContext ApiResult.Error.ValidationError(
                field = "deliveryId",
                message = "Delivery ID is required"
            )
        }
        
        if (password.isBlank()) {
            return@withContext ApiResult.Error.ValidationError(
                field = "password", 
                message = "Password is required"
            )
        }
        
        try {
            val result = deliveryRepository.login(deliveryId, password, languageCode)
            
            result.fold(
                onSuccess = { driverInfo ->
                    // Save session data
                    sessionManager.saveSession(driverInfo)
                    ApiResult.Success(driverInfo)
                },
                onFailure = { exception ->
                    when {
                        exception.message?.contains("network", ignoreCase = true) == true -> 
                            ApiResult.Error.NetworkError(Exception(exception))
                        exception.message?.contains("server", ignoreCase = true) == true -> 
                            ApiResult.Error.ServerError(500, exception.message ?: "Server error")
                        else -> 
                            ApiResult.Error.UnknownError(exception)
                    }
                }
            )
        } catch (e: Exception) {
            ApiResult.Error.UnknownError(e)
        }
    }
}