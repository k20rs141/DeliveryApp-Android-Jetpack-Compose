package com.example.deliveryapp.network

import com.example.deliveryapp.data.CarData
import com.example.deliveryapp.data.LocationData
import com.example.deliveryapp.data.SensorData
import retrofit2.http.GET
import retrofit2.http.Query

interface OhtomiApiService {
    @GET("androidApp/ocs_insert.php")
    suspend fun getLocationData(
        @Query("rate") heartRate: Int,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("t_num") carId: Int,
        @Query("speed") speed: Int,
        @Query("distance") distance: Int,
        @Query("timeGap") timeGap: Int,
        @Query("bearing") bearing: Int,
        @Query("calculatedSpeed") calculatedSpeed: Int,
        @Query("user_acceleration_x") userAccelerationX: Int,
        @Query("user_acceleration_y") userAccelerationY: Int,
        @Query("user_acceleration_z") userAccelerationZ: Int,
        @Query("battery") battery: Int,
        @Query("localTime") localTime: String
    ): List<LocationData>

    @GET("androidApp/ocs_insertIMEI.php")
    suspend fun getDeviceData(
        @Query("IMEI") imei: String,
        @Query("t_num") carId: Int
    ): List<CarData>

    @GET("co2/dbread.php")
    suspend fun getSensorData(
        @Query("carId") carId: Int,
        @Query("limit") limit: Int  // 最新データだけ必要ならlimit=1
    ): List<SensorData>
}