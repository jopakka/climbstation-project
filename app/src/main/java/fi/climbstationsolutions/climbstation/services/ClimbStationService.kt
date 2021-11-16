package fi.climbstationsolutions.climbstation.services

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.*
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import fi.climbstationsolutions.climbstation.ui.MainActivity
import kotlinx.coroutines.*
import java.util.*

class ClimbStationService : Service() {
    companion object {
        private const val TAG = "ClimbStationService"
        private const val GET_INFO_DELAY: Long = 1000

        private const val NOTIFICATION_CHANNEL_ID = "service_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Climbing service"
        private const val NOTIFICATION_CHANNEL_GROUP_ID = "service_group"
        private const val NOTIFICATION_CHANNEL_GROUP_NAME = "Climbing group"

        const val PROFILE_EXTRA = "Profile"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
        const val CLIMB_STATION_SERIAL_EXTRA = "SerialNo"
        const val BROADCAST_INFO_NAME = "ClimbStationService_Info"
        const val BROADCAST_ID_NAME = "ClimbStationService_ID"
        var SERVICE_RUNNING = false
            private set
        var CLIMBING_ACTIVE = false
            private set
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private var nm: NotificationManager? = null
    private var currentStep = 0

    private lateinit var climbStationSerialNo: String
    private lateinit var clientKey: String
    private lateinit var sessionDao: SessionWithDataDao
    private lateinit var profileWithSteps: ClimbProfileWithSteps

    /**
     * Creates notification, initializes variables and starts session.
     * If [intent]s action is [ACTION_STOP] then stops service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action.equals(ACTION_STOP, true)) {
            stopService()
        } else {
            intent?.extras?.let {
                initService(
                    it.getString(CLIMB_STATION_SERIAL_EXTRA, ""),
                    it.getParcelable(PROFILE_EXTRA)
                )
                createNotification()
                beginSession()
            }
        }

        return START_STICKY
    }

    /**
     * Must have [Service] class abstract function override
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Initializes variables
     *
     * @param serialNo ClimbStations serial number
     * @param prof Wanted climbing profile
     */
    private fun initService(serialNo: String, prof: ClimbProfileWithSteps?) {
        SERVICE_RUNNING = true
        climbStationSerialNo = serialNo
        profileWithSteps = prof ?: throw NullPointerException("No profile passed to extras")
        sessionDao = AppDatabase.get(this).sessionDao()
    }

    /**
     * Stops session and log user out of ClimbStation machine. Sets [SERVICE_RUNNING] to false
     */
    private fun stopService() {
        SERVICE_RUNNING = false
        stopClimbStationAndLogout()
        stopForeground(true)
        stopSelf()
    }

    /**
     * Broadcasts bundle named "info", which contains [Int]s "speed", "angle" and "length"
     */
    private fun broadcastValues(speed: Int, angle: Int, length: Int) {
        val intent = Intent(BROADCAST_INFO_NAME)

        val bundle = Bundle()
        bundle.putInt("speed", speed)
        bundle.putInt("angle", angle)
        bundle.putInt("length", length)

        intent.putExtra("info", bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Broadcasts id
     */
    private fun broadcastId(id: Long) {
        val intent = Intent(BROADCAST_ID_NAME)
        intent.putExtra("id", id)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Creates notification for service
     */
    private fun createNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (nm == null)
            nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        nm?.createNotificationChannelGroup(
            NotificationChannelGroup(NOTIFICATION_CHANNEL_GROUP_ID, NOTIFICATION_CHANNEL_GROUP_NAME)
        )

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).also {
            it.enableLights(false)
            it.lockscreenVisibility = Notification.VISIBILITY_SECRET
        }

        nm?.createNotificationChannel(notificationChannel)

        // TODO("Change notification text")
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Climbing in progress")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.app_logo)
            .build()

        startForeground(123, notification)
    }

    /**
     * Login to ClimbStation using [clientKey]. Adds new [Session] to database.
     * Then starts to get info from ClimbStation.
     */
    private fun beginSession() {
        var sessionID: Long? = null

        serviceScope.launch {
            try {
                // Get clientKey from ClimbStation
                clientKey = ClimbStationRepository.login(
                    climbStationSerialNo,
                    BuildConfig.USERNAME,
                    BuildConfig.PASSWORD
                )
                // Save session to database
                val calendar = Calendar.getInstance()
                sessionID = sessionDao.insertSession(
                    Session(
                        0,
                        profileWithSteps.profile.name,
                        calendar.time,
                        profileWithSteps.profile.id
                    )
                )
                Log.d(TAG, "sessionID: $sessionID")

                val started = operateClimbStation("start")
                if (started)
                    getInfoFromClimbStation(sessionID ?: throw Exception("No sessionID"))
                else
                    throw Exception("ClimbStation not started")

                // Set endedAt time to session when it's finished
                sessionID?.let {
                    sessionDao.setEndedAtToSession(it, Calendar.getInstance().time)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Start session error: ${e.localizedMessage}")
                sessionID?.let {
                    sessionDao.deleteSession(it)
                }
                stopService()
            }
        }
    }

    /**
     * Stops ClimbStation and log user out of it.
     */
    private fun stopClimbStationAndLogout() {
        serviceScope.launch {
            try {
                operateClimbStation("stop")
                logoutFromClimbStation()
            } catch (e: Exception) {
                Log.e(TAG, "StopClimbStationAndLogout error: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Operates ClimbStation. Only accepted [operation]s are "start" and "stop".
     */
    private suspend fun operateClimbStation(operation: String): Boolean {
        return try {
            ClimbStationRepository.operation(climbStationSerialNo, clientKey, operation)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Loop of getting info from ClimbStation and [broadcastId]
     */
    private suspend fun getInfoFromClimbStation(sessionID: Long) {
        try {
            setAngle(profileWithSteps.steps[0].angle)
            broadcastId(sessionID)

            while (SERVICE_RUNNING) {
                CLIMBING_ACTIVE = true
                getInfo(sessionID)
                delay(GET_INFO_DELAY)
            }
            CLIMBING_ACTIVE = false
        } catch (e: Exception) {
            Log.e(TAG, "GetInfo error: ${e.localizedMessage}")
            CLIMBING_ACTIVE = false
            stopService()
        }
    }

    /**
     * Get info from CLimbStation. Save that data to database.
     */
    private suspend fun getInfo(sessionID: Long) {
        // Get ClimbStation info
        val info = ClimbStationRepository.deviceInfo(climbStationSerialNo, clientKey)
        Log.d(TAG, "Info: $info")

        val speed = info.speedNow.toInt()
        val angle = info.angleNow.toInt()
        val length = info.length.toInt()

        // Save info to database
        val dID = sessionDao.insertData(Data(0, sessionID, speed, angle, length))
        Log.d(TAG, "dataID: $dID")

        broadcastValues(speed, angle, length)

        adjustToProfile(info.length.toInt())
    }

    private suspend fun adjustToProfile(distance: Int) {
        var step = profileWithSteps.steps[currentStep]
        val stepsSoFar = profileWithSteps.steps.filterIndexed { index, _ -> index < currentStep }
        val distanceSoFar = stepsSoFar.sumOf { it.distance }

        if (distanceSoFar + step.distance >= distance) {
            currentStep += 1

            if (currentStep > profileWithSteps.steps.size) {
                // TODO("What should it do after program")
                // Now it just set wall to 0 angle and stops service
                setAngle(0)
                setSpeed(0)
                stopService()
                return
            }

            step = profileWithSteps.steps[currentStep]
            setAngle(step.angle)
        }
    }

    private suspend fun setAngle(angle: Int) {
        try {
            val response = ClimbStationRepository.setAngle(climbStationSerialNo, clientKey, angle)
            Log.d(TAG, "SetAngle: $response")
        } catch (e: Exception) {
            Log.e(TAG, "SetAngle error: ${e.localizedMessage}")
        }
    }

    private suspend fun setSpeed(speed: Int) {
        try {
            val response = ClimbStationRepository.setSpeed(climbStationSerialNo, clientKey, speed)
            Log.d(TAG, "SetSpeed: $response")
        } catch (e: Exception) {
            Log.e(TAG, "SetSpeed error: ${e.localizedMessage}")
        }
    }

    /**
     * Log user out of ClimbStation
     */
    private fun logoutFromClimbStation() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val logout = ClimbStationRepository.logout(climbStationSerialNo, clientKey)
                Log.d(TAG, "Logout: $logout")
            } catch (e: Exception) {
                Log.e(TAG, "Logout error: ${e.localizedMessage}")
            }
        }
    }
}
