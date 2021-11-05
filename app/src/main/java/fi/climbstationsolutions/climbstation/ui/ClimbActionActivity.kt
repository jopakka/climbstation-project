package fi.climbstationsolutions.climbstation.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClimbActionActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ClimbActionActivity"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
        const val CLIMB_STATION_SERIAL_EXTRA = "SerialNo"
        const val PROFILE_EXTRA = "Profile"
    }

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

        Intent(this, ClimbStationService::class.java).also {
            // TODO("Use SerialNo from sharedPrefs here")
            it.putExtra(CLIMB_STATION_SERIAL_EXTRA, "20110001")
            // TODO("Get right profile")
            it.putExtra(PROFILE_EXTRA, ProfileHandler.readProfiles(this, R.raw.profiles).first())
            startForegroundService(it)
        }
    }
}