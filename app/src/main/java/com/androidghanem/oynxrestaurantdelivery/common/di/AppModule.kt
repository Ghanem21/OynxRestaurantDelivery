package com.androidghanem.oynxrestaurantdelivery.common.di

import com.androidghanem.data.session.SessionExpirationManager
import com.androidghanem.oynxrestaurantdelivery.ui.util.UserActivityTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides UserActivityTracker for detecting user activity
     */
    @Provides
    @Singleton
    fun provideUserActivityTracker(
        sessionExpirationManager: SessionExpirationManager
    ): UserActivityTracker {
        return UserActivityTracker(sessionExpirationManager)
    }
}