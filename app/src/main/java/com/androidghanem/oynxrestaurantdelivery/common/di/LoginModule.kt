package com.androidghanem.oynxrestaurantdelivery.common.di

import com.androidghanem.data.session.SessionManager
import com.androidghanem.domain.features.login.LoginUseCase
import com.androidghanem.domain.repository.DeliveryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Dependency injection module for Login feature
 */
@Module
@InstallIn(ViewModelComponent::class)
object LoginModule {

    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(
        deliveryRepository: DeliveryRepository,
        sessionManager: SessionManager,
    ): LoginUseCase {
        return LoginUseCase(deliveryRepository, sessionManager)
    }
}