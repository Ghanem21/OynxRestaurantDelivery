package com.androidghanem.data.repository

import com.androidghanem.data.network.api.OnyxDeliveryService
import com.androidghanem.data.network.model.request.BaseRequest
import com.androidghanem.data.network.model.request.BillsRequest
import com.androidghanem.data.network.model.request.ChangePasswordRequest
import com.androidghanem.data.network.model.request.LanguageRequest
import com.androidghanem.data.network.model.request.LoginRequest
import com.androidghanem.data.network.model.request.UpdateBillStatusRequest
import com.androidghanem.domain.model.DeliveryBillItem
import com.androidghanem.domain.model.DeliveryDriverInfo
import com.androidghanem.domain.model.DeliveryStatusType
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.constants.LanguageConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val apiService: OnyxDeliveryService
) : DeliveryRepository {

    /**
     * Maps UI language codes to API language codes using centralized constants
     */
    private fun mapLanguageCodeToApi(uiLanguageCode: String): String {
        return LanguageConstants.mapUiToApiLanguage(uiLanguageCode)
    }

    override suspend fun login(
        deliveryId: String,
        password: String,
        languageCode: String
    ): Result<DeliveryDriverInfo> = withContext(Dispatchers.IO) {
        try {
            val request = BaseRequest(
                LoginRequest(
                    P_LANG_NO = mapLanguageCodeToApi(languageCode),
                    P_DLVRY_NO = deliveryId,
                    P_PSSWRD = password
                )
            )
            
            val response = apiService.checkDeliveryLogin(request)
            
            if (response.Result.ErrNo == 0) {
                val deliveryName = response.Data?.DeliveryName ?: ""
                Result.success(DeliveryDriverInfo(deliveryId = deliveryId, name = deliveryName))
            } else {
                Result.failure(Exception(response.Result.ErrMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun changePassword(
        deliveryId: String,
        oldPassword: String,
        newPassword: String,
        languageCode: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = BaseRequest(
                ChangePasswordRequest(
                    P_LANG_NO = mapLanguageCodeToApi(languageCode),
                    P_DLVRY_NO = deliveryId,
                    P_OLD_PSSWRD = oldPassword,
                    P_NEW_PSSWRD = newPassword
                )
            )
            
            val response = apiService.changeDeliveryPassword(request)
            
            if (response.Result.ErrNo == 0) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.Result.ErrMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDeliveryBills(
        deliveryId: String,
        billSerial: String,
        processedFlag: String,
        languageCode: String
    ): Result<List<DeliveryBillItem>> = withContext(Dispatchers.IO) {
        try {
            val request = BaseRequest(
                BillsRequest(
                    P_DLVRY_NO = deliveryId,
                    P_LANG_NO = mapLanguageCodeToApi(languageCode),
                    P_BILL_SRL = billSerial,
                    P_PRCSSD_FLG = processedFlag
                )
            )
            
            val response = apiService.getDeliveryBillsItems(request)
            
            if (response.Result.ErrNo == 0 && response.Data != null) {
                val billItems = mutableListOf<DeliveryBillItem>()
                
                // Handle case where DeliveryBills might be missing in the response
                response.Data.DeliveryBills?.let { deliveryBills ->
                    for (billResponse in deliveryBills) {
                        try {
                            billItems.add(billResponse.toDomain())
                        } catch (e: Exception) {
                            // Skip invalid items
                        }
                    }
                }
                
                return@withContext Result.success(billItems)
            } else {
                val errorMessage = response.Result.ErrMsg.ifEmpty { "Unknown error occurred" }
                return@withContext Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun getDeliveryStatusTypes(
        languageCode: String
    ): Result<List<DeliveryStatusType>> = withContext(Dispatchers.IO) {
        try {
            val request = BaseRequest(
                LanguageRequest(
                    P_LANG_NO = mapLanguageCodeToApi(languageCode)
                )
            )
            
            val response = apiService.getDeliveryStatusTypes(request)
            
            if (response.Result.ErrNo == 0 && response.Data != null) {
                val statusTypes = response.Data.DeliveryStatusTypes?.mapNotNull {
                    try {
                        it.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                return@withContext Result.success(statusTypes)
            } else {
                val errorMessage = response.Result.ErrMsg.ifEmpty { "Unknown error occurred" }
                return@withContext Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun updateDeliveryBillStatus(
        billSerial: String,
        statusFlag: String,
        returnReason: String,
        languageCode: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = BaseRequest(
                UpdateBillStatusRequest(
                    P_LANG_NO = mapLanguageCodeToApi(languageCode),
                    P_BILL_SRL = billSerial,
                    P_DLVRY_STATUS_FLG = statusFlag,
                    P_DLVRY_RTRN_RSN = returnReason
                )
            )
            
            val response = apiService.updateDeliveryBillStatus(request)
            
            if (response.Result.ErrNo == 0) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.Result.ErrMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}