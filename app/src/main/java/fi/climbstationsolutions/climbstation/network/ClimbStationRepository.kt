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
     * Log client out of ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Clients eky
     * @return [Boolean] Did logout success
     */
    suspend fun logout(climbStationSerialNo: String, clientKey: String): Boolean {
        try {
            val req = LogoutRequest(climbStationSerialNo, clientKey)
            val response = call.logout(req)
//            Log.d(TAG, "Logout: $response")
            response.response?.let {
                if(it == "OK") return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Logout error: ${e.localizedMessage}")
        }
        return false
    }

    /**
     * Gets info about ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @return [InfoResponse] Info about unit
     * @throws Exception Throws if response is not "OK" or something else went wrong
     */
    suspend fun deviceInfo(climbStationSerialNo: String, clientKey: String): InfoResponse {
        try {
            val req = InfoRequest(climbStationSerialNo, clientKey)
            val response = call.deviceInfo(req)
//            Log.d(TAG, "DeviceInfo: $response")
            response.response?.let {
                if (it == "OK") return response
            }
        } catch (e: Exception) {
            Log.e(TAG, "DeviceInfo error: ${e}")
        }
        throw Exception("Response not ok")
    }

    /**
     * Starts or stops ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @param operation "start" or "stop"
     * @return [Boolean] Did operation was successful
     */
    suspend fun operation(climbStationSerialNo: String, clientKey: String, operation: String): Boolean {
        try {
            if(operation != "start" && operation != "stop")
                throw IllegalArgumentException("Operation must be \"start\" or \"stop\"")

            val req = OperationRequest(climbStationSerialNo, clientKey, operation)
            val response = call.operation(req)
//            Log.d(TAG, "$response")
            response.response?.let {
                if(it == "OK") return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Operation error: ${e.localizedMessage}")
        }
        return false
    }
}