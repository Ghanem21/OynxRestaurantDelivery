package com.androidghanem.data.local.db

import android.content.Context
import com.androidghanem.data.local.db.dao.OnyxDeliveryDao

/**
 * Module to provide database dependencies
 */
object DatabaseModule {
    
    /**
     * Provides the OnyxDeliveryDao
     */
    fun provideOnyxDeliveryDao(context: Context): OnyxDeliveryDao {
        return OnyxDeliveryDatabase.getInstance(context).onyxDeliveryDao()
    }
}