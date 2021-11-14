package fi.climbstationsolutions.climbstation.network

import retrofit2.http.Body
import retrofit2.http.POST

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