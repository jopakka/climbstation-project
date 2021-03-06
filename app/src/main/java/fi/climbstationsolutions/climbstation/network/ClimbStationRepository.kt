package fi.climbstationsolutions.climbstation.network

import android.util.Log
import androidx.annotation.IntRange
import fi.climbstationsolutions.climbstation.BuildConfig

/**
 * @author Joonas Niemi
 */
object ClimbStationRepository {
    private const val TAG = "Network"
    private val call = ClimbStationAPI.get(BuildConfig.CLIMBSTATION_URL)

    /**
     * Gets clientKey from ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param userID Users ID
     * @param password Users password
     * @return [String] ClientKey or null
     * @throws NullPointerException No clientKey
     * @throws Exception Something else went wrong
     */
    suspend fun login(climbStationSerialNo: String, userID: String, password: String): String {
        return try {
            val req = LoginRequest(climbStationSerialNo, userID, password)
            val response = call.login(req)
            response.clientKey ?: throw NullPointerException("No clientKey")
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
     * @throws Exception Something else went wrong
     */
    suspend fun logout(climbStationSerialNo: String, clientKey: String): Boolean {
        return try {
            val req = LogoutRequest(climbStationSerialNo, clientKey)
            val response = call.logout(req)
            response.response?.equals("OK") ?: false
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
     * @throws NullPointerException Response in not ok
     * @throws Exception Something else went wrong
     */
    suspend fun deviceInfo(climbStationSerialNo: String, clientKey: String): InfoResponse {
        return try {
            val req = InfoRequest(climbStationSerialNo, clientKey)
            val response = call.deviceInfo(req)
            if (response.response?.equals("OK") == true) response
            else throw NullPointerException("Response not ok")
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
     * @return [Boolean] Was operation successful
     * @throws IllegalArgumentException Throws if [operation] is not "start" or "stop"
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
            response.response?.equals("OK") ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Operation error: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Set speed for ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @param speed Wanted speed (mm per second)
     * @return [Boolean] Was operation successful
     * @throws Exception Something else went wrong
     */
    suspend fun setSpeed(
        climbStationSerialNo: String,
        clientKey: String,
        @IntRange(from = 0) speed: Int
    ): Boolean {
        return try {
            val req = SpeedRequest(climbStationSerialNo, clientKey, speed.toString())
            val response = call.setSpeed(req)
            response.response?.equals("OK") ?: false
        } catch (e: Exception) {
            Log.e(TAG, "SetSpeed error: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Set angle for ClimbStation.
     *
     * @param climbStationSerialNo Serialnumber of ClimbStation unit
     * @param clientKey Client specific key for verifying user
     * @param angle Wanted angle (in degrees, -45 to +45)
     * @return [Boolean] Was operation successful
     * @throws Exception Something else went wrong
     */
    suspend fun setAngle(
        climbStationSerialNo: String,
        clientKey: String,
        @IntRange(from = -45, to = 45) angle: Int
    ): Boolean {
        return try {
            val req = AngleRequest(climbStationSerialNo, clientKey, angle.toString())
            val response = call.setAngle(req)
            response.response?.equals("OK") ?: false
        } catch (e: Exception) {
            Log.e(TAG, "SetSpeed error: ${e.localizedMessage}")
            throw e
        }
    }
}