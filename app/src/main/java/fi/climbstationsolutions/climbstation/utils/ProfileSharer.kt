package fi.climbstationsolutions.climbstation.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.network.profile.Profile
import java.io.*

class ProfileSharer(private val activity: Activity) {
    private val gson = Gson()

    fun shareProfile(profile: ClimbProfileWithSteps) {
        val json = gson.toJson(profile)
        val file = createFile(profile.profile.name)
        writeToFile(file, json)

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

    fun getSharedProfile(uri: Uri): ClimbProfileWithSteps? {
        val data = readFromFile(uri)
        return try {
            Gson().fromJson(data, ClimbProfileWithSteps::class.java)
        } catch (e: Exception) { null }
    }

    private fun createFile(name: String): File {
        val outputDir = activity.cacheDir
        return File.createTempFile(name, ".json", outputDir)
    }

    private fun writeToFile(file: File, data: String) {
        val stream = FileOutputStream(file)
        stream.use {
            it.write(data.toByteArray())
        }
    }

    private fun readFromFile(uri: Uri): String {
        val stream = activity.contentResolver.openInputStream(uri)
        val writer = StringWriter()
        val buffer = CharArray(1024)

        stream.use { s ->
            val reader = BufferedReader(InputStreamReader(s, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }

        return writer.toString()
    }
}