package fi.climbstationsolutions.climbstation.services

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.*
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import fi.climbstationsolutions.climbstation.ui.MainActivity
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor

/**
 * Service for fetching information from ClimbStation and controlling it
 * while user is climbing.
 */
class ClimbStationService : Service() {
    companion object {
        private const val TAG = "ClimbStationService"
        private const val GET_INFO_DELAY: Long = 1000

        private const val NOTIFICATION_CHANNEL_ID = "service_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Climbing service"
        private const val NOTIFICATION_CHANNEL_GROUP_ID = "service_group"
        private const val NOTIFICATION_CHANNEL_GROUP_NAME = "Climbing group"

        const val PROFILE_EXTRA = "Profile"
        const val TIMER_EXTRA = "Timer"
        const val EXTRA_ERROR = "ErrorMessage"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
        const val CLIMB_STATION_SERIAL_EXTRA = "SerialNo"
        const val BROADCAST_ID_NAME = "ClimbStationService_ID"
        const val BROADCAST_FINISHED = "ClimbStationService_Finished"
        const val BROADCAST_ERROR = "ClimbStationService_Error"
        const val BROADCAST_ERROR_CLIMB = "ClimbStationService_Error_Climb"
        var SERVICE_RUNNING = false
            private set
        var CLIMBING_ACTIVE = false
            private set
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var nm: NotificationManager? = null
    private var currentStep = 0
    private var startTime: Long = 0L
    private var timer: Int? = null // by milliseconds
    private var tts: Tts? = null
    private var nextDistanceToNotify = 0
    private var nextTimeToNotify = 0
    private var climbStationSerialNo: String? = null
    private lateinit var clientKey: String
    private lateinit var sessionDao: SessionWithDataDao
    private lateinit var profileWithSteps: ClimbProfileWithSteps
    private var localBroadcastManager: LocalBroadcastManager? = null

    // Values which control how often text-to-speech will notify user
    private val distanceNotifyRange = 5 // by meters
    private val timeNotifyRange = 1 // by minutes

    /**
     * Creates notification, initializes variables and starts session.
     * If [intent]s action is [ACTION_STOP] then stops service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action.equals(ACTION_STOP, true)) {
            stopService()
        } else {
            intent?.extras?.let {
                val profile = it.getParcelable(PROFILE_EXTRA) as? ClimbProfileWithSteps
                initTts()
                initService(
                    it.getString(CLIMB_STATION_SERIAL_EXTRA, null),
                    profile,
                    it.getInt(TIMER_EXTRA, -1)
                )
                createNotification(profile)
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
    private fun initService(serialNo: String, prof: ClimbProfileWithSteps?, timer: Int) {
        SERVICE_RUNNING = true
        climbStationSerialNo = serialNo
        profileWithSteps = prof ?: throw NullPointerException("No profile passed to extras")
        if (timer != -1) {
            this.timer = timer
        }
        sessionDao = AppDatabase.get(this).sessionDao()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
    }

    /**
     * Initializes [Tts] for service and values for it
     */
    private fun initTts() {
        tts = Tts(this)
        nextDistanceToNotify = distanceNotifyRange
        nextTimeToNotify = timeNotifyRange
    }

    /**
     * Stops session and log user out of ClimbStation machine. Sets [SERVICE_RUNNING] to false
     */
    private fun stopService() {
        tts?.destroy()
        climbStationSerialNo?.let {
            stopClimbStationAndLogout(it)
        }
        stopForeground(true)
        stopSelf()
        SERVICE_RUNNING = false
    }

    /**
     * Broadcasts session id
     */
    private fun broadcastId(id: Long) {
        val intent = Intent(BROADCAST_ID_NAME)
        intent.putExtra("id", id)
        localBroadcastManager?.sendBroadcast(intent)
    }

    /**
     * Broadcast when session is finished
     */
    private fun broadcastFinished() {
        val intent = Intent(BROADCAST_FINISHED)
        intent.putExtra("finished", true)
        localBroadcastManager?.sendBroadcast(intent)
    }

    /**
     * Broadcast for generic errors
     */
    private fun broadcastError(message: String = "") {
        val intent = Intent(BROADCAST_ERROR)
        intent.putExtra(EXTRA_ERROR, message)
        localBroadcastManager?.sendBroadcast(intent)
    }

    /**
     * Broadcast for climb errors
     */
    private fun broadcastClimbError(message: String = "") {
        val intent = Intent(BROADCAST_ERROR_CLIMB)
        intent.putExtra(EXTRA_ERROR, message)
        localBroadcastManager?.sendBroadcast(intent)
    }

    /**
     * Creates notification for service
     */
    private fun createNotification(profileWithSteps: ClimbProfileWithSteps?) {
        // Intent which navigates to climbOn fragment when notification is clicked
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_main)
            .setDestination(R.id.climbOnFragment)
            .setArguments(Bundle().also {
                it.putParcelable("profileWithSteps", profileWithSteps)
            })
            .createPendingIntent()

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

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
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
                val serialNo = climbStationSerialNo
                if (serialNo == null || serialNo == "") {
                    throw NoSuchFieldException(getString(R.string.error_no_serial))
                }
                // Get clientKey from ClimbStation
                clientKey = ClimbStationRepository.login(
                    serialNo,
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

                val started = operateClimbStation("start", serialNo)
                if (started)
                    getInfoFromClimbStation(sessionID ?: throw Exception("No sessionID"), serialNo)
                else
                    throw Exception("ClimbStation not started")

                // Set endedAt time to session when it's finished
                setEndTimeForSession(sessionID)
                broadcastFinished()
            } catch (e: Exception) {
                Log.e(TAG, "Start session error: ${e.localizedMessage}")
                sessionID?.let {
                    sessionDao.deleteSession(it)
                }
                broadcastError(e.localizedMessage ?: getString(R.string.error_while_connecting))
            } finally {
                stopService()
            }
        }
    }

    /**
     * Stops ClimbStation and log user out of it.
     */
    private fun stopClimbStationAndLogout(serialNo: String) {
        serviceScope.launch {
            try {
                operateClimbStation("stop", serialNo)
                logoutFromClimbStation(serialNo)
            } catch (e: Exception) {
                Log.e(TAG, "StopClimbStationAndLogout error: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Operates ClimbStation. Only accepted [operation]s are "start" and "stop".
     */
    private suspend fun operateClimbStation(operation: String, serialNo: String): Boolean {
        return try {
            ClimbStationRepository.operation(serialNo, clientKey, operation)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Loop of getting info from ClimbStation and [broadcastId]
     */
    private suspend fun getInfoFromClimbStation(sessionID: Long, serialNo: String) {
        try {
            startTime = Calendar.getInstance().timeInMillis
            setAngle(profileWithSteps.steps[0].angle, serialNo)
            broadcastId(sessionID)

            while (SERVICE_RUNNING && !checkTimeFinished(Calendar.getInstance().timeInMillis - startTime)) {
                CLIMBING_ACTIVE = true
                getInfo(sessionID, serialNo)
                delay(GET_INFO_DELAY)
            }
            CLIMBING_ACTIVE = false
        } catch (e: Exception) {
            Log.e(TAG, "GetInfo error: ${e.localizedMessage}")
            CLIMBING_ACTIVE = false
            broadcastClimbError(getString(R.string.error_while_getting_info))
            stopService()
        }
    }

    /**
     * Get info from CLimbStation. Save that data to database.
     */
    private suspend fun getInfo(sessionID: Long, serialNo: String) {
        // Get ClimbStation info
        val info = ClimbStationRepository.deviceInfo(serialNo, clientKey)

        val speed = info.speedNow.toInt()
        val angle = info.angleNow.toInt()
        val length = info.length.toInt()

        distanceNotifier(length)
        val time = Calendar.getInstance().timeInMillis
        timeNotifier(time - startTime)

        // Save info to database
        sessionDao.insertData(Data(0, sessionID, speed, angle, length))

        adjustToProfile(info.length.toInt(), serialNo)
    }

    /**
     * Adjusts ClimbStations angle to [profileWithSteps], if user is climbed enough.
     */
    private suspend fun adjustToProfile(distance: Int, serialNo: String) {
        var step = profileWithSteps.steps[currentStep]
        val stepsSoFar = profileWithSteps.steps.filterIndexed { index, _ -> index < currentStep }
        val distanceSoFar = stepsSoFar.sumOf { it.distance }

        if (distance != 0 && distanceSoFar + step.distance >= distance) {
            currentStep += 1

            if (currentStep > profileWithSteps.steps.size) {
                // TODO("What should it do after program")
                // Now it just set wall to 0 angle and stops service
                setAngle(0, serialNo)
                setSpeed(0, serialNo)
                broadcastFinished()
                stopService()
                return
            }

            step = profileWithSteps.steps[currentStep]
            setAngle(step.angle, serialNo)
        }
    }

    /**
     * Sends setAngle request to machine
     */
    private suspend fun setAngle(angle: Int, serialNo: String) {
        try {
            ClimbStationRepository.setAngle(serialNo, clientKey, angle)
        } catch (e: Exception) {
            Log.e(TAG, "SetAngle error: ${e.localizedMessage}")
        }
    }

    /**
     * Sends setSpeed request to machine
     */
    private suspend fun setSpeed(speed: Int, serialNo: String) {
        try {
            ClimbStationRepository.setSpeed(serialNo, clientKey, speed)
        } catch (e: Exception) {
            Log.e(TAG, "SetSpeed error: ${e.localizedMessage}")
        }
    }

    /**
     * Log user out of ClimbStation
     */
    private fun logoutFromClimbStation(serialNo: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                ClimbStationRepository.logout(serialNo, clientKey)
            } catch (e: Exception) {
                Log.e(TAG, "Logout error: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Sets end time for [Session] if [sessionID] is not null
     */
    private suspend fun setEndTimeForSession(sessionID: Long?) {
        sessionID?.let {
            sessionDao.setEndedAtToSession(it, Calendar.getInstance().time)
        }
    }

    /**
     * Notifies user about how much (s)he is climbed, if [distance] to meters
     * is greater than [nextDistanceToNotify]
     */
    private fun distanceNotifier(distance: Int) {
        val meters = floor(distance / 1000f).toInt()
        if (meters >= nextDistanceToNotify) {
            nextDistanceToNotify = meters + distanceNotifyRange
            tts?.speak(
                resources.getQuantityString(
                    R.plurals.speech_time_climbed_plural,
                    meters, meters
                )
            )
        }
    }

    /**
     * Notifies user about how much (s)he is climbed, if [time] to minutes
     * is greater than [nextTimeToNotify]
     */
    private fun timeNotifier(time: Long) {
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1)).toInt()
        if (minutes >= nextTimeToNotify) {
            nextTimeToNotify = (minutes + timeNotifyRange)
            tts?.speak(
                resources.getQuantityString(
                    R.plurals.speech_time_climbed_plural,
                    minutes, minutes
                )
            )
        }
    }

    /**
     * @return true if [timer] is not null and it's greater than 0 else false
     */
    private fun checkTimeFinished(elapsed: Long): Boolean {
        val t = timer
        return if (t != null && t > 0) {
            elapsed >= t
        } else false
    }
}
