package fi.climbstationsolutions.climbstation.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object ClimbStationAPI {
    // TODO("Change to 'http://192.168.3.1:8800/' when publishing app")
    private const val BASE_URL = "http://192.168.1.11:8800/"

    interface Service {
        @POST("login")
        suspend fun login(@Body req: LoginRequest): LoginResponse

        @POST("logout")
        suspend fun logout(@Body req: LogoutRequest): ClimbStationGenericResponse

        @POST("climbstationinfo")
        suspend fun deviceInfo(@Body req: InfoRequest): InfoResponse

        @POST("Operation")
        suspend fun operation(@Body req: OperationRequest): ClimbStationGenericResponse

        @POST("setspeed")
        suspend fun setSpeed(@Body req: SpeedRequest): ClimbStationGenericResponse

        @POST("setangle")
        suspend fun setAngle(@Body req: AngleRequest): ClimbStationGenericResponse
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(LoggerInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service = retrofit.create(Service::class.java)
}