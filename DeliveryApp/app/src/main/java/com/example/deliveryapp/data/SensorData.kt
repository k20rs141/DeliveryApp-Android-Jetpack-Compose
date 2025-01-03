package com.example.deliveryapp.data

data class SensorData(
    val co2: String,
    val temperature: Float,
    val humidity: Float,
    val pressure: Float,
    val build: Int,
    val systemVersion: String,
    val deviceId: String,
    val deviceName: String,
    val iPhone: Int,
    val lowPower: Int,
    val autoCalibration: Int,
    val wifiEnd: Int,
    val co2Sensor: Int,
    val temperatureSensor: Int,
    val rssi: Int,
    val carId: Int,
    val isFront: Int,
    val modified: String
)
