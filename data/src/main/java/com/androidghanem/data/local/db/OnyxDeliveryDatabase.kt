package com.androidghanem.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.androidghanem.data.local.db.dao.OnyxDeliveryDao
import com.androidghanem.data.local.db.entity.DeliveryEntity
import com.androidghanem.data.local.db.entity.OrderEntity

@Database(
    entities = [DeliveryEntity::class, OrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OnyxDeliveryDatabase : RoomDatabase() {
    
    abstract fun onyxDeliveryDao(): OnyxDeliveryDao
    
    companion object {
        @Volatile
        private var INSTANCE: OnyxDeliveryDatabase? = null
        
        fun getInstance(context: Context): OnyxDeliveryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OnyxDeliveryDatabase::class.java,
                    "onyx_delivery_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}