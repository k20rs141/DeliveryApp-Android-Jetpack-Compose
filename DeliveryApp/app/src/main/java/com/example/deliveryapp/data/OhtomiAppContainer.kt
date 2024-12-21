package com.example.deliveryapp.data

import com.example.deliveryapp.model.NetworkOhtomiRepository
import com.example.deliveryapp.model.OhtomiRepository
import com.example.deliveryapp.network.OhtomiApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val ohtomiRepository: OhtomiRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://ohtomi.apps.kyusan-u.ac.jp/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: OhtomiApiService by lazy {
        retrofit.create(OhtomiApiService::class.java)
    }

    override val ohtomiRepository: OhtomiRepository by lazy {
        NetworkOhtomiRepository(retrofitService)
    }
}
