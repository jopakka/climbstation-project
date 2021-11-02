package fi.climbstationsolutions.climbstation.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import org.w3c.dom.Text

class ClimbActionActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ClimbActionActivity"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
        const val CLIMB_STATION_SERIAL_EXTRA = "SerialNo"
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
        if (!ClimbStationService.SERVICE_RUNNING) return
        Log.d(TAG, "Stop climbing")

        Intent(this, ClimbStationService::class.java).also {
            it.action = ACTION_STOP
            startForegroundService(it)
        }
    }

    private fun startClimbing() {
        if (ClimbStationService.SERVICE_RUNNING) return
        Log.d(TAG, "Start climbing")

        Intent(this, ClimbStationService::class.java).also {
            it.putExtra(CLIMB_STATION_SERIAL_EXTRA, "20110001")
            startForegroundService(it)
        }
    }
}