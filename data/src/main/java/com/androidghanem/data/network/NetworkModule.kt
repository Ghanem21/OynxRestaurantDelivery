package com.androidghanem.data.network

import com.androidghanem.data.network.api.OnyxDeliveryService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network module for providing Retrofit services
 */
object NetworkModule {
    
    private const val BASE_URL = "https://mdev.yemensoft.net:473/"
    
    /**
     * Creates an OkHttpClient with logging
     */
    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Creates a Moshi instance for JSON parsing
     */
    private fun createMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    /**
     * Creates a Retrofit instance
     */
    private fun createRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    /**
     * Provides OnyxDeliveryService API interface
     */
    fun provideOnyxDeliveryService(): OnyxDeliveryService {
        val okHttpClient = createOkHttpClient()
        val moshi = createMoshi()
        val retrofit = createRetrofit(okHttpClient, moshi)
        
        return retrofit.create(OnyxDeliveryService::class.java)
    }
}