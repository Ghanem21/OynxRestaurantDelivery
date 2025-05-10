package com.androidghanem.data.network.api

import com.androidghanem.data.network.model.request.BaseRequest
import com.androidghanem.data.network.model.request.BillsRequest
import com.androidghanem.data.network.model.request.LoginRequest
import com.androidghanem.data.network.model.response.BaseResponse
import com.androidghanem.data.network.model.response.DeliveryBillsWrapper
import com.androidghanem.data.network.model.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for OnyxDeliveryService
 */
interface OnyxDeliveryService {

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

}