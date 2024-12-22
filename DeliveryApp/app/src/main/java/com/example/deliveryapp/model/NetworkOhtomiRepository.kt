package com.example.deliveryapp.model

import com.example.deliveryapp.data.LocationData
import com.example.deliveryapp.data.SensorData
import com.example.deliveryapp.network.OhtomiApiService
import okhttp3.ResponseBody
import retrofit2.Response

interface OhtomiRepository {
    suspend fun postDeviceToken(deviceToken: String)
    suspend fun getLocationData(
        heartRate: Int,
        lat: Double,
        lon: Double,
        carId: Int,
        speed: Int,
        distance: Int,
        timeGap: Int,
        bearing: Int,
        calculatedSpeed: Int,
        userAccelerationX: Int,
        userAccelerationY: Int,
        userAccelerationZ: Int,
        battery: Int,
        localTime: String
    ): Response<ResponseBody>
    suspend fun getDeviceData(imie: String, carId: Int): Int
    suspend fun getSensorData(carId: Int, limit: Int): List<SensorData>
}


class NetworkOhtomiRepository(
    private val deviceTokenApiService: OhtomiApiService,
    private val ohtomiApiService: OhtomiApiService
) : OhtomiRepository {
    override suspend fun postDeviceToken(deviceToken: String) {}

    override suspend fun getLocationData(
        heartRate: Int,
        lat: Double,
        lon: Double,
        carId: Int,
        speed: Int,
        distance: Int,
        timeGap: Int,
        bearing: Int,
        calculatedSpeed: Int,
        userAccelerationX: Int,
        userAccelerationY: Int,
        userAccelerationZ: Int,
        battery: Int,
        localTime: String
    ): Response<ResponseBody> = ohtomiApiService.getLocationData(
        heartRate = heartRate,
        lat = lat,
        lon = lon,
        carId = carId,
        speed = speed,
        distance = distance,
        timeGap = timeGap,
        bearing = bearing,
        calculatedSpeed = calculatedSpeed,
        userAccelerationX = userAccelerationX,
        userAccelerationY = userAccelerationY,
        userAccelerationZ = userAccelerationZ,
        battery = battery,
        localTime = localTime
    )

    override suspend fun getDeviceData(imei: String, carId: Int): Int = ohtomiApiService.getDeviceData(
        imei = imei,
        carId = carId
    )

    override suspend fun getSensorData(carId: Int, limit: Int): List<SensorData> = ohtomiApiService.getSensorData(
        carId = carId,
        limit = limit
    )
}
