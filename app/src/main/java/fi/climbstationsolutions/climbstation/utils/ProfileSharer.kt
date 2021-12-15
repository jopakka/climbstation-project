package fi.climbstationsolutions.climbstation.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import java.io.*
import java.util.*

/**
 * Class which handles profile sharing
 *
 * @author Joonas Niemi
 */
class ProfileSharer(private val activity: Activity) {
    private val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi.adapter(ClimbProfileWithSteps::class.java)

    /**
     * Shares [profile] on JSON format and send it to where user want to share it
     */
    fun shareProfile(profile: ClimbProfileWithSteps) {
        val json = jsonAdapter.toJson(profile)
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

    /**
     * Reads [ClimbProfileWithSteps] from [uri]
     */
    fun getSharedProfile(uri: Uri): ClimbProfileWithSteps? {
        val data = readFromFile(uri)
        return try {
            jsonAdapter.fromJson(data)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Creates new json file with name [name]
     */
    private fun createFile(name: String): File {
        val outputDir = activity.cacheDir
        return File.createTempFile(name, ".json", outputDir)
    }

    /**
     * Writes [data] to [file]
     */
    private fun writeToFile(file: File, data: String) {
        val stream = FileOutputStream(file)
        stream.use {
            it.write(data.toByteArray())
        }
    }

    /**
     * Reads data from [uri] and returns it as [String]
     */
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