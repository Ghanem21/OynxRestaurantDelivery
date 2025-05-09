package com.androidghanem.data.network.api

import com.androidghanem.data.network.model.request.*
import com.androidghanem.data.network.model.response.BaseResponse
import com.androidghanem.data.network.model.response.DeliveryBillResponse
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
    @POST("OnyxDeliveryService/Service.svc/ChangeDeliveryPassword")
    suspend fun changeDeliveryPassword(
        @Body request: BaseRequest<ChangePasswordRequest>
    ): BaseResponse<Any>

    /**
     * Check delivery login
     */
    @POST("OnyxDeliveryService/Service.svc/CheckDeliveryLogin")
    suspend fun checkDeliveryLogin(
        @Body request: BaseRequest<LoginRequest>
    ): BaseResponse<LoginResponse>

    /**
     * Get delivery bills items
     */
    @POST("OnyxDeliveryService/Service.svc/GetDeliveryBillsItems")
    suspend fun getDeliveryBillsItems(
        @Body request: BaseRequest<BillsRequest>
    ): BaseResponse<DeliveryBillsWrapper>

    /**
     * Get delivery status types
     */
    @POST("OnyxDeliveryService/Service.svc/GetDeliveryStatusTypes")
    suspend fun getDeliveryStatusTypes(
        @Body request: BaseRequest<LanguageRequest>
    ): BaseResponse<DeliveryStatusTypesWrapper>

    /**
     * Get return bill reasons
     */
    @POST("OnyxDeliveryService/Service.svc/GetReturnBillReasons")
    suspend fun getReturnBillReasons(
        @Body request: BaseRequest<LanguageRequest>
    ): BaseResponse<List<Any>>

    /**
     * Update delivery bill status
     */
    @POST("OnyxDeliveryService/Service.svc/UpdateDeliveryBillStatus")
    suspend fun updateDeliveryBillStatus(
        @Body request: BaseRequest<UpdateBillStatusRequest>
    ): BaseResponse<Any>
}