package fi.climbstationsolutions.climbstation.services

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.Data
import fi.climbstationsolutions.climbstation.database.Session
import fi.climbstationsolutions.climbstation.database.SessionWithDataDao
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity.Companion.ACTION_STOP
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

        var SERVICE_RUNNING = false
    }

    private var nm: NotificationManager? = null
    private var serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var climbStationSerialNo: String
    private lateinit var clientKey: String
    private lateinit var sessionDao: SessionWithDataDao

    override fun onCreate() {
        super.onCreate()
        SERVICE_RUNNING = true
    }

    override fun onDestroy() {
        super.onDestroy()
        SERVICE_RUNNING = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action.equals(ACTION_STOP, true)) {
            stopCurrentService()
        } else {
            createNotification()
            initService()
            beginSession(climbStationSerialNo)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun initService() {
        SERVICE_RUNNING = true
        climbStationSerialNo = "20110001"
        sessionDao = AppDatabase.get(this).sessionDao()
    }

    private fun stopCurrentService() {
        SERVICE_RUNNING = false
        stopClimbStationAndLogout()
        stopForeground(true)
        stopSelf()
    }

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
            NotificationManager.IMPORTANCE_MIN
        ).also {
            it.enableLights(false)
            it.lockscreenVisibility = Notification.VISIBILITY_SECRET
        }

        nm?.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Climbing in progress")
            .setContentText("You are climbing now")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.cslogo_wh_red)
            .build()

        startForeground(123, notification)
    }

    private fun beginSession(serialNo: String, sessionName: String = "Climbing") {
        var sessionID: Long? = null

        serviceScope.launch {
            try {
                // Get clientKey from ClimbStation
                clientKey = ClimbStationRepository.login(serialNo, "user", "climbstation")
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
                stopCurrentService()
            }
        }
    }

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

    private suspend fun operateClimbStation(operation: String): Boolean {
        return try {
            ClimbStationRepository.operation(climbStationSerialNo, clientKey, operation)
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getInfoFromClimbStation(sessionID: Long) {
        try {
            while (SERVICE_RUNNING) {
                getInfo(sessionID)
                delay(GET_INFO_DELAY)
            }
        } catch (e: Exception) {
            Log.e(TAG, "GetInfo error: ${e.localizedMessage}")
            stopCurrentService()
        }
    }

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
    }

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
