package fi.climbstationsolutions.climbstation.services

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.IntRange
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.Data
import fi.climbstationsolutions.climbstation.database.Session
import fi.climbstationsolutions.climbstation.database.SessionWithDataDao
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import fi.climbstationsolutions.climbstation.network.profile.Profile
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity.Companion.ACTION_STOP
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity.Companion.CLIMB_STATION_SERIAL_EXTRA
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity.Companion.PROFILE_EXTRA
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

        const val BROADCAST_NAME = "ClimbStationService"
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
    private lateinit var profile: Profile

    /**
     * Creates notification, initializes variables and starts session.
     * If [intent]s action is [ACTION_STOP] then stops service.
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action != null && intent.action.equals(ACTION_STOP, true)) {
            stopService()
        } else {
            intent.extras?.let {
                createNotification()
                initService(
                    it.getString(CLIMB_STATION_SERIAL_EXTRA, ""),
                    it.getParcelable(PROFILE_EXTRA)
                )
                beginSession()
            }
        }

        return START_STICKY
    }

    /**
     * Cancels [serviceJob] to avoid memory leak
     */
    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
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
    private fun initService(serialNo: String, prof: Profile?) {
        SERVICE_RUNNING = true
        climbStationSerialNo = serialNo
        profile = prof ?: throw NullPointerException("No profile passed to extras")
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
     * Creates new [Intent]. Then adds extras to it and send broadcast.
     */
    private fun sendInfoToActivity() {
        val intent = Intent(BROADCAST_NAME)
        intent.putExtra("Status", "Ok")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Creates notification for service
     */
    private fun createNotification() {
        val notificationIntent = Intent(this, ClimbActionActivity::class.java)
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
    private fun beginSession(sessionName: String = "Climb") {
        var sessionID: Long? = null

        serviceScope.launch {
            try {
                // Get clientKey from ClimbStation
                // TODO("Maybe change userID and password to be in some file")
                clientKey = ClimbStationRepository.login(climbStationSerialNo, "user", "climbstation")
                // Save session to database
                val calendar = Calendar.getInstance()
                sessionID = sessionDao.insertSession(Session(0, sessionName, calendar.time))
                Log.d(TAG, "sessionID: $sessionID")

                val started = operateClimbStation("start")
                if (started)
                    getInfoFromClimbStation(sessionID ?: throw Exception("No sessionID"))
                else
                    throw Exception("ClimbStation not started")
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
     * Loop of getting info from ClimbStation and [sendInfoToActivity]
     */
    private suspend fun getInfoFromClimbStation(sessionID: Long) {
        try {
            Log.d(TAG, "Profile: $profile")

            setAngle(profile.steps[0].angle)

            while (SERVICE_RUNNING) {
                CLIMBING_ACTIVE = true
                getInfo(sessionID)
                sendInfoToActivity()
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

        // Save info to database
        val dID = sessionDao.insertData(
            Data(
                0,
                sessionID,
                info.speedNow.toInt(),
                info.angleNow.toInt(),
                info.length.toInt()
            )
        )
        Log.d(TAG, "dataID: $dID")

        adjustToProfile(info.length.toInt())
    }

    private suspend fun adjustToProfile(distance: Int) {
        var step = profile.steps[currentStep]
        val stepsSoFar = profile.steps.filterIndexed { index, _ -> index < currentStep }
        val distanceSoFar = stepsSoFar.sumOf { it.distance }

        if(distanceSoFar + step.distance >= distance) {
            currentStep += 1

            if(currentStep > profile.steps.size) {
                // TODO("What should it do after program")
                // Now it just set wall to 0 angle and stops service
                setAngle(0)
                setSpeed(0)
                stopService()
                return
            }

            step = profile.steps[currentStep]
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
