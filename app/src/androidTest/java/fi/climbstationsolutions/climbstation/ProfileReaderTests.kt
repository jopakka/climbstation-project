package fi.climbstationsolutions.climbstation

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileReaderTests {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun profileReader() {
        val profiles = ProfileHandler.readProfiles(context, R.raw.profiles)
        assertTrue(profiles.isNotEmpty())
    }

    @Test(expected = Exception::class)
    fun profileReaderThrow() {
        ProfileHandler.readProfiles(context, -1)
    }
}