package com.example.deliveryapp.data

data class LocationData(
    val heartRate: Int,
    val lat: Double,
    val lon: Double,
    val carId: Int,
    val speed: Int,
    val distance: Int,
    val timeGap: Int,
    val bearing: Int,
    val calculatedSpeed: Int,
    val userAccelerationX: Int,
    val userAccelerationY: Int,
    val userAccelerationZ: Int,
    val battery: Int,
    val localTime: String
)
