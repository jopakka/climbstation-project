package fi.climbstationsolutions.climbstation.network

import com.google.gson.annotations.SerializedName

/**
 * Every http request body contains these
 */
interface ClimbStationRequest {
    val packetID: String
    val packetNumber: String
    val climbStationSerialNo: String
}

/**
 * Every http response body contains these
 */
interface ClimbStationResponse {
    val response: String?
}

/**
 * Generic response used in some http queries
 */
data class ClimbStationGenericResponse(
    @SerializedName("Response") override val response: String?
) : ClimbStationResponse

/**
 * Request body for login
 */
data class LoginRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    @SerializedName("UserID") val userID: String,
    val password: String,
    @SerializedName("PacketID") override val packetID: String = "2a",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

/**
 * Response body for login
 */
data class LoginResponse(
    @SerializedName("Response") override val response: String?,
    val clientKey: String?,
) : ClimbStationResponse

/**
 * Request body for logout
 */
data class LogoutRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Logout") val logout: String = "request",
    @SerializedName("PacketID") override val packetID: String = "2g",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

/**
 * Request body for info
 */
data class InfoRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("ClimbingData") val climbingData: String = "request",
    @SerializedName("PacketID") override val packetID: String = "2b",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

/**
 * Response body for info
 */
data class InfoResponse(
    @SerializedName("Response") override val response: String?,
    @SerializedName("Length") val length: String,
    @SerializedName("AngleNow") val angleNow: String,
    @SerializedName("SpeedNow") val speedNow: String,
) : ClimbStationResponse

/**
 * Request body for operations
 */
data class OperationRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Operation") val operation: String,
    @SerializedName("PacketID") override val packetID: String = "2c",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

/**
 * Request body for setSpeed
 */
data class SpeedRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Speed") val speed: String,
    @SerializedName("PacketID") override val packetID: String = "2d",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

/**
 * Request body for setAngle
 */
data class AngleRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Angle") val angle: String,
    @SerializedName("PacketID") override val packetID: String = "2e",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest
