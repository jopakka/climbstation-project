package fi.climbstationsolutions.climbstation.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import fi.climbstationsolutions.climbstation.utils.WallProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GraphTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_test)


        initGraph()
    }

    private fun initGraph() {
        val wall = findViewById<WallProfile>(R.id.wall_profile)
        lifecycleScope.launch(Dispatchers.Default) {

            val del = 25L
            while (true) {
                wall.profile = ProfileHandler.readProfiles(this@GraphTestActivity, R.raw.profiles)[1]
                wall.climbingProgression = 0f
                while (wall.climbingProgression < 100f) {
                    wall.climbingProgression += 1f
                    delay(del)
                }
                wall.profile = ProfileHandler.readProfiles(this@GraphTestActivity, R.raw.profiles)[5]
                wall.climbingProgression = 0f
                while (wall.climbingProgression < 100f) {
                    wall.climbingProgression += 1f
                    delay(del)
                }
                wall.profile = ProfileHandler.readProfiles(this@GraphTestActivity, R.raw.profiles).last()
                wall.climbingProgression = 0f
                while (wall.climbingProgression < 100f) {
                    wall.climbingProgression += 1f
                    delay(del)
                }
            }
        }
    }
}