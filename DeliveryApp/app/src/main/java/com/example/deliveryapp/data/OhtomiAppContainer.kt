package com.example.deliveryapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.deliveryapp.model.DeviceInfoRepository
import com.example.deliveryapp.model.DeviceInfoRepositoryImpl
import com.example.deliveryapp.model.OhtomiRepository
import com.example.deliveryapp.model.OhtomiRepositoryImpl
import com.example.deliveryapp.network.DeviceTokenApiService
import com.example.deliveryapp.network.OhtomiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceTokenRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OhtomiRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val deviceTokenUrl = "http://133.17.165.165:8086/"
    private val ohtomiUrl = "https://ohtomi.apps.kyusan-u.ac.jp/"

    @Provides
    @Singleton
    @DeviceTokenRetrofit
    fun deviceTokenRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(deviceTokenUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @OhtomiRetrofit
    fun ohtomiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ohtomiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDeviceTokenApiService(@DeviceTokenRetrofit retrofit: Retrofit): DeviceTokenApiService {
        return retrofit.create(DeviceTokenApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOhtomiApiService(@OhtomiRetrofit retrofit: Retrofit): OhtomiApiService {
        return retrofit.create(OhtomiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        deviceTokenApiService: DeviceTokenApiService,
        ohtomiApiService: OhtomiApiService
    ): OhtomiRepository {
        return OhtomiRepositoryImpl(deviceTokenApiService, ohtomiApiService)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("device_info") }
        )
    }

    @Provides
    @Singleton
    fun provideDeviceInfoRepository(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>
    ): DeviceInfoRepository {
        return DeviceInfoRepositoryImpl(context, dataStore)
    }
}