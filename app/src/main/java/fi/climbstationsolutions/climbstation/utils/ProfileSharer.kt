package fi.climbstationsolutions.climbstation.utils

import android.app.Activity
import android.content.Intent
import androidx.core.content.FileProvider
import com.google.gson.Gson
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import java.io.File
import java.io.FileOutputStream

class ProfileSharer(private val activity: Activity) {
    private val gson = Gson()

    fun shareProfile(profile: ClimbProfileWithSteps) {
        val json = gson.toJson(profile)

        val outputDir = activity.cacheDir
        val file = File.createTempFile(profile.profile.name, ".json", outputDir)

        val stream = FileOutputStream(file)
        stream.use {
            it.write(json.toByteArray())
        }
        val uri = FileProvider.getUriForFile(
            activity,
            "fi.climbstationsolutions.climbstation.fileprovider",
            file
        )

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/json"
            putExtra(Intent.EXTRA_TEXT, "Check out my custom profile")
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        activity.startActivity(sendIntent)
    }
}