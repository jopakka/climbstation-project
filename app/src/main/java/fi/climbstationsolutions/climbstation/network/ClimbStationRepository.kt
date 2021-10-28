package fi.climbstationsolutions.climbstation.network

import android.util.Log

object ClimbStationRepository {
    private const val TAG = "Network"
    private val call = ClimbStationAPI.service

    /**
     * Gets clientKey from ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param userID Users ID
     * @param password Users password
     * @return [String] ClientKey
     * @throws NullPointerException if there is no clientKey
     */
    suspend fun login(climbStationSerialNo: String, userID: String, password: String): String {
        try {
            val req = LoginRequest(climbStationSerialNo, userID, password)
            val response = call.login(req)
//            Log.d(TAG, "$response")
            response.clientKey?.let {
                return it
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.localizedMessage}")
        }
        throw NullPointerException("No clientKey")
    }

    /**
     * Gets info about ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @return [InfoResponse] Info about unit
     * @throws NullPointerException Throws if response is not "OK" or something else went wrong
     */
    suspend fun deviceInfo(climbStationSerialNo: String, clientKey: String): InfoResponse {
        try {
            val req = InfoRequest(climbStationSerialNo, clientKey)
            val response = call.deviceInfo(req)
//            Log.d(TAG, "$response")
            response.response?.let {
                if (it != "OK") throw NullPointerException()
                return response
            }
        } catch (e: Exception) {
//            Log.e(TAG, "DeviceInfo error: ${e.localizedMessage}")
        }
        throw NullPointerException("Response not ok")
    }
}