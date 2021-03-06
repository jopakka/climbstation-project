package fi.climbstationsolutions.climbstation.network.profile

import android.content.Context
import android.os.Parcelable
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter

/**
 * @author Joonas Niemi
 */
object ProfileHandler {
    /**
     * Opens a raw file and reads it content to string.
     * Then returns [List] of [Profile]
     */
    fun readProfiles(context: Context, @RawRes resId: Int): List<Profile> {
        val raw = context.resources.openRawResource(resId)
        val writer = StringWriter()
        val buffer = CharArray(1024)

        raw.use { rawData ->
            val reader = BufferedReader(InputStreamReader(rawData, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }

        val jsonString: String = writer.toString()
        val type = object : TypeToken<List<Profile>>() {}.type
        return Gson().fromJson(jsonString, type)
    }
}

/**
 * Data class for default profile steps.
 */
@Parcelize
data class Step(
    val step: Int,
    val distance: Int,
    val angle: Int
) : Parcelable

/**
 * Data class for default profiles.
 */
@Parcelize
data class Profile(
    val level: Int,
    val name: String,
    val steps: @RawValue List<Step>
) : Parcelable