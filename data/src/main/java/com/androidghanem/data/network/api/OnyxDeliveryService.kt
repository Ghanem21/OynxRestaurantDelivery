package com.androidghanem.data.network.api

import com.androidghanem.data.network.model.request.BaseRequest
import com.androidghanem.data.network.model.request.BillsRequest
import com.androidghanem.data.network.model.request.ChangePasswordRequest
import com.androidghanem.data.network.model.request.LanguageRequest
import com.androidghanem.data.network.model.request.LoginRequest
import com.androidghanem.data.network.model.request.UpdateBillStatusRequest
import com.androidghanem.data.network.model.response.BaseResponse
import com.androidghanem.data.network.model.response.DeliveryBillsWrapper
import com.androidghanem.data.network.model.response.DeliveryStatusTypesWrapper
import com.androidghanem.data.network.model.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for OnyxDeliveryService
 */
interface OnyxDeliveryService {

    /**
     * Change delivery password
     */
    @POST("ChangeDeliveryPassword")
    suspend fun changeDeliveryPassword(
        @Body request: BaseRequest<ChangePasswordRequest>
    ): BaseResponse<Any>

    /**
     * Check delivery login
     */
    @POST("CheckDeliveryLogin")
    suspend fun checkDeliveryLogin(
        @Body request: BaseRequest<LoginRequest>
    ): BaseResponse<LoginResponse>

    /**
     * Get delivery bills items
     */
    @POST("GetDeliveryBillsItems")
    suspend fun getDeliveryBillsItems(
        @Body request: BaseRequest<BillsRequest>
    ): BaseResponse<DeliveryBillsWrapper>

    /**
     * Get delivery status types
     */
    @POST("GetDeliveryStatusTypes")
    suspend fun getDeliveryStatusTypes(
        @Body request: BaseRequest<LanguageRequest>
    ): BaseResponse<DeliveryStatusTypesWrapper>

    /**
     * Get return bill reasons
     */
    @POST("GetReturnBillReasons")
    suspend fun getReturnBillReasons(
        @Body request: BaseRequest<LanguageRequest>
    ): BaseResponse<List<Any>>

    /**
     * Update delivery bill status
     */
    @POST("UpdateDeliveryBillStatus")
    suspend fun updateDeliveryBillStatus(
        @Body request: BaseRequest<UpdateBillStatusRequest>
    ): BaseResponse<Any>
}