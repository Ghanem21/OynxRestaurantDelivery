package com.androidghanem.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.androidghanem.domain.model.DeliveryDriverInfo

@Entity(tableName = "deliveries")
data class DeliveryEntity(
    @PrimaryKey
    val deliveryId: String,
    val name: String
) {

    companion object {
        fun fromDomain(driverInfo: DeliveryDriverInfo): DeliveryEntity {
            return DeliveryEntity(
                deliveryId = driverInfo.deliveryId,
                name = driverInfo.name
            )
        }
    }
}