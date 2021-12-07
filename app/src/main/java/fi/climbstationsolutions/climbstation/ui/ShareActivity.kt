package fi.climbstationsolutions.climbstation.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.utils.ProfileSharer

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        readData()
    }

    private fun readData() {
        val path = intent?.data ?: return
        val sharer = ProfileSharer(this)
        val test = sharer.getSharedProfile(path)
        Log.d("TEST", "${test?.profile}")
    }
}