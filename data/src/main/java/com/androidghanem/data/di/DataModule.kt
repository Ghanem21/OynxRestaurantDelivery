package com.androidghanem.data.di

import android.content.Context
import com.androidghanem.data.local.db.DatabaseModule
import com.androidghanem.data.local.db.dao.OnyxDeliveryDao
import com.androidghanem.data.network.api.OnyxDeliveryService
import com.androidghanem.data.preferences.AppPreferencesManager
import com.androidghanem.data.repository.DeliveryRepositoryCachedImpl
import com.androidghanem.data.repository.DeliveryRepositoryImpl
import com.androidghanem.data.repository.LanguageRepositoryImpl
import com.androidghanem.data.session.SessionExpirationManager
import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.repository.DeliveryRepository
import com.androidghanem.domain.repository.LanguageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppPreferencesManager(@ApplicationContext context: Context): AppPreferencesManager {
        return AppPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideOnyxDeliveryDao(@ApplicationContext context: Context): OnyxDeliveryDao {
        return DatabaseModule.provideOnyxDeliveryDao(context)
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideSessionExpirationManager(
        @ApplicationContext context: Context,
        sessionManager: SessionManager
    ): SessionExpirationManager {
        return SessionExpirationManager(context, sessionManager)
    }

    @Provides
    @Singleton
    fun provideDeliveryRepositoryImpl(
        apiService: OnyxDeliveryService
    ): DeliveryRepositoryImpl {
        return DeliveryRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(
        repositoryImpl: DeliveryRepositoryImpl,
        dao: OnyxDeliveryDao
    ): DeliveryRepository {
        return DeliveryRepositoryCachedImpl(repositoryImpl, dao)
    }

    @Provides
    @Singleton
    fun provideLanguageRepository(
        preferencesManager: AppPreferencesManager
    ): LanguageRepository {
        return LanguageRepositoryImpl(preferencesManager)
    }
}