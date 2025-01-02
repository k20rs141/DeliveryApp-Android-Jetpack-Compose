package com.example.deliveryapp.network

import com.example.deliveryapp.data.SensorData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface DeviceTokenApiService {
    @Headers("Content-Type: text/plain")
    @POST("ohtomi/deviceTokenA.php")
    suspend fun postDeviceToken(
        @Body deviceToken: String
    ) : Response<Unit>
}

interface OhtomiApiService {
    @GET("androidApp/ocs_insert.php")
    suspend fun sendLocation(
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
    ) : Response<Unit>

    @GET("androidApp/ocs_insertIMEI.php")
    suspend fun getDevice(
        @Query("IMEI") imei: String,
        @Query("t_num") carId: Int
    ) : Response<Int>

    @GET("co2/dbread.php")
    suspend fun getSensor(
        @Query("carId") carId: Int,
        @Query("limit") limit: Int  // 最新データだけ必要ならlimit=1
    ) : Response<List<SensorData>>
}