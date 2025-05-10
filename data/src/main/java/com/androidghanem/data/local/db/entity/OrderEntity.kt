package com.androidghanem.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.androidghanem.domain.model.Order
import com.androidghanem.domain.model.OrderStatus

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = DeliveryEntity::class,
            parentColumns = ["deliveryId"],
            childColumns = ["deliveryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deliveryId")]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val deliveryId: String,
    val statusCode: Int,
    val totalPrice: String,
    val date: String
) {
    fun toDomain(): Order {
        val status = when (statusCode) {
            0 -> OrderStatus.NEW
            1 -> OrderStatus.DELIVERED
            2 -> OrderStatus.PARTIAL_RETURN
            3 -> OrderStatus.RETURNED
            4 -> OrderStatus.DELIVERING
            else -> OrderStatus.NEW
        }
        
        return Order(
            id = id,
            status = status,
            totalPrice = totalPrice,
            date = date
        )
    }
    
    companion object {
        fun fromDomain(order: Order, deliveryId: String): OrderEntity {
            val statusCode = when (order.status) {
                OrderStatus.NEW -> 0
                OrderStatus.DELIVERING -> 4
                OrderStatus.DELIVERED -> 1
                OrderStatus.PARTIAL_RETURN -> 2
                OrderStatus.RETURNED -> 3
            }
            
            return OrderEntity(
                id = order.id,
                deliveryId = deliveryId,
                statusCode = statusCode,
                totalPrice = order.totalPrice,
                date = order.date
            )
        }
    }
}