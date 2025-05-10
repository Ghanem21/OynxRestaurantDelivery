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
    
    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId")
    fun getAllOrders(deliveryId: String): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND statusCode = 0")
    fun getNewOrders(deliveryId: String): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE deliveryId = :deliveryId AND statusCode != 0")
    fun getProcessedOrders(deliveryId: String): Flow<List<OrderEntity>>
    
    @Query("DELETE FROM orders WHERE deliveryId = :deliveryId")
    suspend fun deleteOrdersForDelivery(deliveryId: String)
}