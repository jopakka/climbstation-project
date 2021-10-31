package fi.climbstationsolutions.climbstation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.services.ClimbStationService

class ClimbActionActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ClimbActionActivity"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_climb_action)

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopClimbing()
        }

        startClimbing()
    }

    private fun stopClimbing() {
        if (!ClimbStationService.SERVICE_RUNNING) return
        Log.d(TAG, "Stop climbing")

        val stopIntent = Intent(this, ClimbStationService::class.java).also {
            it.action = ACTION_STOP
        }
        startService(stopIntent)
    }

    private fun startClimbing() {
        if (ClimbStationService.SERVICE_RUNNING) return
        Log.d(TAG, "Start climbing")

        val startIntent = Intent(this, ClimbStationService::class.java)
        startForegroundService(startIntent)
    }
}