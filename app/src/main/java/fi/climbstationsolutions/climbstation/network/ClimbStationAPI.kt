package fi.climbstationsolutions.climbstation.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object ClimbStationAPI {
    private const val BASE_URL = "http://192.168.1.13:8800/"

    interface Service {
        @POST("login")
        suspend fun login(@Body req: LoginRequest): LoginResponse

        @POST("logout")
        suspend fun logout(@Body req: LogoutRequest): ClimbStationGenericResponse

        @POST("climbstationinfo")
        suspend fun deviceInfo(@Body req: InfoRequest): InfoResponse

        @POST("Operation")
        suspend fun operation(@Body req: OperationRequest): ClimbStationGenericResponse
    }

    private val interceptor = HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.HEADERS }

    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service = retrofit.create(Service::class.java)
}