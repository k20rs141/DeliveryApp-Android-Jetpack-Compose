package com.example.deliveryapp.model

import com.example.deliveryapp.data.LocationData
import com.example.deliveryapp.data.SensorData
import com.example.deliveryapp.network.OhtomiApiService
import javax.inject.Inject
import javax.inject.Singleton

interface OhtomiRepository {
    suspend fun postDeviceToken(deviceToken: String): Result<Unit>
    suspend fun sendLocationData(locationData: LocationData): Result<Unit>
    suspend fun getDeviceData(imei: String, carId: Int): Result<Int>
    suspend fun getSensorData(carId: Int, limit: Int): Result<List<SensorData>>
}

@Singleton
class OhtomiRepositoryImpl @Inject constructor(
    private val ohtomiApiService: OhtomiApiService
): OhtomiRepository {
    override suspend fun postDeviceToken(deviceToken: String): Result<Unit> {
        return try {
            val response = ohtomiApiService.postDeviceToken(deviceToken)
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendLocationData(locationData: LocationData): Result<Unit> {
        return try {
            val response = ohtomiApiService.sendLocation(
                heartRate = locationData.heartRate,
                lat = locationData.lat,
                lon = locationData.lon,
                carId = locationData.carId,
                speed = locationData.speed,
                distance = locationData.distance,
                timeGap = locationData.timeGap,
                bearing = locationData.bearing,
                calculatedSpeed = locationData.calculatedSpeed,
                userAccelerationX = locationData.userAccelerationX,
                userAccelerationY = locationData.userAccelerationY,
                userAccelerationZ = locationData.userAccelerationZ,
                battery = locationData.battery,
                localTime = locationData.localTime
            )
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceData(imei: String, carId: Int): Result<Int> {
        return try {
            val response = ohtomiApiService.getDevice(imei, carId)
            if (response.isSuccessful) {
                val carId = response.body()
                if (carId != null) {
                    Result.success(carId)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSensorData(carId: Int, limit: Int): Result<List<SensorData>> {
        return try {
            val response = ohtomiApiService.getSensor(carId, limit)
            if (response.isSuccessful) {
                val sensorDataList = response.body()
                if (sensorDataList != null) {
                    Result.success(sensorDataList)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}