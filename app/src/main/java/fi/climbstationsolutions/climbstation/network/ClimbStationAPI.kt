package fi.climbstationsolutions.climbstation.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.POST

object ClimbStationAPI {
    private const val BASE_URL = "http://192.168.1.13:8800/"

    interface Service {
        @POST("login")
        suspend fun login(@Body req: LoginRequest): LoginResponse

        @HTTP(method = "POST", path = "climbstationinfo", hasBody = true)
        suspend fun deviceInfo(@Body req: InfoRequest): InfoResponse
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service = retrofit.create(Service::class.java)
}