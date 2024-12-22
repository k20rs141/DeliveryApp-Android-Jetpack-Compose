package com.example.deliveryapp.network

import com.example.deliveryapp.data.LocationData
import com.example.deliveryapp.data.SensorData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface OhtomiApiService {
    @Headers("Content-Type: text/plain")
    @POST("ohtomi/deviceTokenA.php")
    suspend fun postDeviceToken(
        @Body deviceToken: String
    )

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
    ): Response<ResponseBody>

    @GET("androidApp/ocs_insertIMEI.php")
    suspend fun getDeviceData(
        @Query("IMEI") imei: String,
        @Query("t_num") carId: Int
    ): Int

    @GET("co2/dbread.php")
    suspend fun getSensorData(
        @Query("carId") carId: Int,
        @Query("limit") limit: Int  // 最新データだけ必要ならlimit=1
    ): List<SensorData>
}