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
     * @throws NoSuchFieldException There is no clientKey
     * @throws Exception Something else went wrong
     */
    suspend fun login(climbStationSerialNo: String, userID: String, password: String): String {
        return try {
            val req = LoginRequest(climbStationSerialNo, userID, password)
            val response = call.login(req)
            //            Log.d(TAG, "$response")

            response.clientKey ?: throw NoSuchFieldException("No clientKey")
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Log client out of ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @return [Boolean] Did logout success
     * @throws NoSuchFieldException There is no response
     * @throws Exception Something else went wrong
     */
    suspend fun logout(climbStationSerialNo: String, clientKey: String): Boolean {
        return try {
            val req = LogoutRequest(climbStationSerialNo, clientKey)
            val response = call.logout(req)
            //            Log.d(TAG, "Logout: $response")

            response.response?.equals("OK")
                ?: throw NoSuchFieldException("Response is not valid")
        } catch (e: Exception) {
            Log.e(TAG, "Logout error: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Gets info about ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @return [InfoResponse] Info about unit
     * @throws NoSuchFieldException There is no response
     * @throws Exception Something else went wrong
     */
    suspend fun deviceInfo(climbStationSerialNo: String, clientKey: String): InfoResponse {
        return try {
            val req = InfoRequest(climbStationSerialNo, clientKey)
            val response = call.deviceInfo(req)
//            Log.d(TAG, "DeviceInfo: $response")

            if (response.response?.equals("OK") == true) response
            else throw NoSuchFieldException("Response is not valid")
        } catch (e: Exception) {
            Log.e(TAG, "DeviceInfo error: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Starts or stops ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @param operation "start" or "stop"
     * @return [Boolean] Did operation was successful
     * @throws IllegalArgumentException Operation is not "start" or "stop"
     * @throws NoSuchFieldException There is no response
     * @throws Exception Something else went wrong
     */
    suspend fun operation(
        climbStationSerialNo: String,
        clientKey: String,
        operation: String
    ): Boolean {
        return try {
            if (operation != "start" && operation != "stop")
                throw IllegalArgumentException("Operation must be \"start\" or \"stop\"")

            val req = OperationRequest(climbStationSerialNo, clientKey, operation)
            val response = call.operation(req)
//            Log.d(TAG, "$response")

            response.response?.equals("OK") ?: throw NoSuchFieldException("Response is not valid")
        } catch (e: Exception) {
            Log.e(TAG, "Operation error: ${e.localizedMessage}")
            throw e
        }
    }
}