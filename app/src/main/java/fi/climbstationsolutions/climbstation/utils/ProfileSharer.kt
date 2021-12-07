package fi.climbstationsolutions.climbstation.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class ProfileSharer(private val activity: Activity) {
    private val gson = Gson()

    fun shareProfile(profile: ClimbProfileWithSteps) {
        val json = gson.toJson(profile)

        val outputDir = activity.cacheDir
        val file = File.createTempFile("profileTest", ".json", outputDir)

        val stream = FileOutputStream(file)
        stream.use {
            it.write(json.toByteArray())
        }
        val uri = FileProvider.getUriForFile(activity, "fi.climbstationsolutions.climbstation.fileprovider", file)

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/json"
            putExtra(Intent.EXTRA_TEXT, "Check out my custom profile")
            putExtra(Intent.EXTRA_STREAM, uri)
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        activity.startActivity(sendIntent)
    }
}