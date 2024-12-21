package com.example.deliveryapp

import android.app.Application
import com.example.deliveryapp.data.AppContainer
import com.example.deliveryapp.data.DefaultAppContainer

class OhtomiApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}