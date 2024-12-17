package com.example.deliveryapp.data

data class SensorData(
    val location: String,
    val co2: String,
    val temperature: Float,
    val humidity: Float,
    val pressure: Float,
    val build: Int,
    val systemVersion: String,
    val deviceId: String,
    val deviceName: String,
    val wifiEnd: Int,
    val carId: Int,
    val isFront: Int,
    val modified: String
)
