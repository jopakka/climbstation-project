package fi.climbstationsolutions.climbstation.network

import com.google.gson.annotations.SerializedName

interface ClimbStationGeneric {
    val packetID: String
    val packetNumber: String
}

interface ClimbStationRequest : ClimbStationGeneric {
    val climbStationSerialNo: String
}

interface ClimbStationResponse : ClimbStationGeneric {
    val response: String?
}

data class LoginRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    @SerializedName("UserID") val userID: String,
    val password: String,
    @SerializedName("PacketID") override val packetID: String = "2a",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

data class LoginResponse(
    @SerializedName("PacketID") override val packetID: String,
    @SerializedName("PacketNumber") override val packetNumber: String,
    @SerializedName("Response") override val response: String?,
    val clientKey: String?,
) : ClimbStationResponse

data class InfoRequest(
    @SerializedName("ClimbstationSerialNo") override val climbStationSerialNo: String,
    val clientKey: String,
    @SerializedName("ClimbingData") val climbingData: String = "request",
    @SerializedName("PacketID") override val packetID: String = "2b",
    @SerializedName("PacketNumber") override val packetNumber: String = "1",
) : ClimbStationRequest

data class InfoResponse(
    @SerializedName("PacketID") override val packetID: String,
    @SerializedName("PacketNumber") override val packetNumber: String,
    @SerializedName("Response") override val response: String?,
    @SerializedName("Length") val length: String?,
    @SerializedName("AngleNow") val angleNow: String?,
    @SerializedName("SpeedNow") val speedNow: String?,
) : ClimbStationResponse
