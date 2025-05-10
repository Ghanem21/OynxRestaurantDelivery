package com.androidghanem.data.repository

import android.util.Log
import com.androidghanem.data.local.db.dao.OnyxDeliveryDao
import com.androidghanem.data.local.db.entity.DeliveryEntity
import com.androidghanem.data.local.db.entity.OrderEntity
import com.androidghanem.domain.model.DeliveryBillItem
import com.androidghanem.domain.model.DeliveryDriverInfo
import com.androidghanem.domain.model.DeliveryStatusType
import com.androidghanem.domain.model.Order
import com.androidghanem.domain.repository.DeliveryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of DeliveryRepository that adds caching capabilities using Room database
 */
class DeliveryRepositoryCachedImpl @Inject constructor(
    private val apiRepository: DeliveryRepositoryImpl,
    private val dao: OnyxDeliveryDao
) : DeliveryRepository {

    override suspend fun login(
        deliveryId: String,
        password: String,
        languageCode: String
    ): Result<DeliveryDriverInfo> = withContext(Dispatchers.IO) {
        // Call API first
        val apiResult = apiRepository.login(deliveryId, password, languageCode)
        
        // Cache successful login data
        if (apiResult.isSuccess) {
            apiResult.getOrNull()?.let { driverInfo ->
                dao.insertDelivery(DeliveryEntity.fromDomain(driverInfo))
            }
        }
        
        return@withContext apiResult
    }
    
    override suspend fun changePassword(
        deliveryId: String,
        oldPassword: String,
        newPassword: String,
        languageCode: String
    ): Result<Boolean> {
        // Pass through to API repository
        return apiRepository.changePassword(deliveryId, oldPassword, newPassword, languageCode)
    }
    
    override suspend fun getDeliveryBills(
        deliveryId: String,
        billSerial: String,
        processedFlag: String,
        languageCode: String
    ): Result<List<DeliveryBillItem>> = withContext(Dispatchers.IO) {
        // Call API first
        val apiResult = apiRepository.getDeliveryBills(
            deliveryId, billSerial, processedFlag, languageCode
        )
        
        // Cache successful response
        if (apiResult.isSuccess) {
            apiResult.getOrNull()?.let { bills ->
                try {
                    // First, ensure the delivery entity exists
                    val existingDelivery = dao.getDelivery(deliveryId)
                    if (existingDelivery == null) {
                        // Create a minimal DeliveryEntity if it doesn't exist
                        val deliveryEntity = DeliveryEntity(
                            deliveryId = deliveryId,
                            name = deliveryId // Use ID as name if unknown
                        )
                        dao.insertDelivery(deliveryEntity)
                    }
                    
                    // Now it's safe to insert orders
                    // Convert bills to orders and cache
                    val orders = bills.map { bill ->
                        bill.toOrder()
                    }
                    
                    // Update cache - first clear previous orders then insert new ones
                    dao.deleteOrdersForDelivery(deliveryId)
                    
                    // Map to entities and insert
                    val orderEntities = orders.map { order ->
                        OrderEntity.fromDomain(order, deliveryId)
                    }
                    dao.insertOrders(orderEntities)
                } catch (e: Exception) {
                    // Log but don't fail if caching has issues
                    Log.e("DeliveryRepositoryCached", "Error caching data: ${e.message}")
                }
            }
        }
        
        return@withContext apiResult
    }
    
    override suspend fun getDeliveryStatusTypes(languageCode: String): Result<List<DeliveryStatusType>> {
        // Pass through to API repository - no caching needed
        return apiRepository.getDeliveryStatusTypes(languageCode)
    }
    
    override suspend fun updateDeliveryBillStatus(
        billSerial: String,
        statusFlag: String,
        returnReason: String,
        languageCode: String
    ): Result<Boolean> {
        // Pass through to API repository
        return apiRepository.updateDeliveryBillStatus(billSerial, statusFlag, returnReason, languageCode)
    }
    
    // New methods for accessing cached data
    
    /**
     * Get all orders for a delivery from local cache
     */
    fun getOrdersFromCache(deliveryId: String): Flow<List<Order>> {
        return dao.getAllOrders(deliveryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Get only new orders (status = 0) for a delivery from local cache
     */
    fun getNewOrdersFromCache(deliveryId: String): Flow<List<Order>> {
        return dao.getNewOrders(deliveryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Get only processed orders (status != 0) for a delivery from local cache
     */
    fun getProcessedOrdersFromCache(deliveryId: String): Flow<List<Order>> {
        return dao.getProcessedOrders(deliveryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}