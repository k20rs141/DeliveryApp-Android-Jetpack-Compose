package com.example.deliveryapp.data

import com.example.deliveryapp.model.NetworkOhtomiRepository
import com.example.deliveryapp.model.OhtomiRepository
import com.example.deliveryapp.network.OhtomiApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val ohtomiRepository: OhtomiRepository
}

class OhtomiAppContainer : AppContainer {
    private val deviceTokenUrl = "http://133.17.165.165:8086/"
    private val ohtomiUrl = "https://ohtomi.apps.kyusan-u.ac.jp/"

    private val deviceTokenRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(deviceTokenUrl)
        .build()

    private val ohtomiRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(ohtomiUrl)
        .build()

    private val deviceTokenApiService: OhtomiApiService by lazy {
        deviceTokenRetrofit.create(OhtomiApiService::class.java)
    }

    private val ohtomiApiService: OhtomiApiService by lazy {
        ohtomiRetrofit.create(OhtomiApiService::class.java)
    }

    override val ohtomiRepository: OhtomiRepository by lazy {
        NetworkOhtomiRepository(deviceTokenApiService, ohtomiApiService)
    }
}
