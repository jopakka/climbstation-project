package fi.climbstationsolutions.climbstation.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.services.ClimbStationService.Companion.BROADCAST_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClimbActionActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ClimbActionActivity"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
        const val CLIMB_STATION_SERIAL_EXTRA = "SerialNo"
        const val PROFILE_EXTRA = "Profile"
    }

    private lateinit var broadcastManager: LocalBroadcastManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_climb_action)

        val txtClimbing = findViewById<TextView>(R.id.txtClimbing).apply {
            text = ""
        }

        findViewById<Button>(R.id.btnClimb).apply {
            setOnClickListener {
                if(ClimbStationService.SERVICE_RUNNING) {
                    text = getString(R.string.start)
                    txtClimbing.text = ""
                    stopClimbing()
                } else {
                    text = getString(R.string.stop)
                    txtClimbing.text = "Go to climb"
                    startClimbing()
                }
            }
        }

        broadcastManager = LocalBroadcastManager.getInstance(this).apply {
            registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_NAME))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("Status")
            Log.d("BroadcastReceiver", "Message: $message")
        }
    }

    private fun stopClimbing() {
        Log.d(TAG, "Stop climbing")

        Intent(this, ClimbStationService::class.java).also {
            it.action = ACTION_STOP
            startForegroundService(it)
        }
    }

    private fun startClimbing() {
        Log.d(TAG, "Start climbing")

        val serial = PreferenceHelper.customPrefs(this, PREF_NAME)[SERIAL_NO_PREF_NAME, ""]

        Intent(this, ClimbStationService::class.java).also {
            it.putExtra(CLIMB_STATION_SERIAL_EXTRA, serial)
            // TODO("Get right profile")
            it.putExtra(PROFILE_EXTRA, ProfileHandler.readProfiles(this, R.raw.profiles).first())
            startForegroundService(it)
        }
    }
}