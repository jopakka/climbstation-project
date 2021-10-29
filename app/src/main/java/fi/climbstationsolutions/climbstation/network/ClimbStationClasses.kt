package fi.climbstationsolutions.climbstation.network

import com.google.gson.annotations.SerializedName

interface ClimbStationBasics {
    val packetID: String
    val packetNumber: String
}

interface ClimbStationRequest : ClimbStationBasics {
    val climbStationSerialNo: String
}

interface ClimbStationResponse {
    val response: String?
}

data class ClimbStationGenericResponse(
    @SerializedName("Response") override val response: String?
) : ClimbStationResponse

data class LoginRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    @SerializedName("UserID") val userID: String,
    val password: String,
    @SerializedName("PacketID") override val packetID: String = "2a",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

data class LoginResponse(
    @SerializedName("Response") override val response: String?,
    val clientKey: String?,
) : ClimbStationResponse

data class LogoutRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Logout") val logout: String = "request",
    @SerializedName("PacketID") override val packetID: String = "2g",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

data class InfoRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("ClimbingData") val climbingData: String = "request",
    @SerializedName("PacketID") override val packetID: String = "2b",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

data class InfoResponse(
    @SerializedName("Response") override val response: String?,
    @SerializedName("Length") val length: String?,
    @SerializedName("AngleNow") val angleNow: String?,
    @SerializedName("SpeedNow") val speedNow: String?,
) : ClimbStationResponse

data class OperationRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Operation") val operation: String,
    @SerializedName("PacketID") override val packetID: String = "2c",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

data class SpeedRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Speed") val speed: String,
    @SerializedName("PacketID") override val packetID: String = "2d",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

data class AngleRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("Angle") val angle: String,
    @SerializedName("PacketID") override val packetID: String = "2e",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest
