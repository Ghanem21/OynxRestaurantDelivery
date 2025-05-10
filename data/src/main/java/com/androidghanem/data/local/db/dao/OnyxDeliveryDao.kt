package com.androidghanem.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidghanem.data.local.db.entity.DeliveryEntity
import com.androidghanem.data.local.db.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OnyxDeliveryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)
    
    @Query("SELECT * FROM deliveries WHERE deliveryId = :deliveryId")
    suspend fun getDelivery(deliveryId: String): DeliveryEntity?

    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND statusCode = 0")
    fun getNewOrders(deliveryId: String): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND statusCode != 0")
    fun getProcessedOrders(deliveryId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND date LIKE :datePattern")
    fun getOrdersByDate(deliveryId: String, datePattern: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND totalPrice BETWEEN :minPrice AND :maxPrice")
    fun getOrdersByPriceRange(
        deliveryId: String,
        minPrice: String,
        maxPrice: String,
    ): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND statusCode = :statusCode")
    fun getOrdersByStatus(deliveryId: String, statusCode: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND id LIKE :searchQuery OR totalPrice LIKE :searchQuery")
    fun searchOrders(deliveryId: String, searchQuery: String): Flow<List<OrderEntity>>
    
    @Query("DELETE FROM orders WHERE deliveryId = :deliveryId")
    suspend fun deleteOrdersForDelivery(deliveryId: String)
}