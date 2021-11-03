package fi.climbstationsolutions.climbstation

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirstTimeUITests {
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    private var defaultSerialNo: String? = null

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        defaultSerialNo = prefs.getString(SERIAL_NO_PREF_NAME, null)
        prefs.edit().putString(SERIAL_NO_PREF_NAME, null).commit()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
        prefs.edit().putString(SERIAL_NO_PREF_NAME, defaultSerialNo).commit()
    }

    @Test
    fun initActivityVisible() {
        onView(withId(R.id.txtInfo))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnNext))
            .check(matches(isDisplayed()))
    }

    @Test
    fun nextButtonWork() {
        onView(withId(R.id.btnNext)).perform(click())
        onView(withId(R.id.etSerialNo)).check(matches(isDisplayed()))
    }

    @Test
    fun enterFalseSerialNo() {
        onView(withId(R.id.btnNext)).perform(click())
        onView(withId(R.id.etSerialNo))
            .perform(typeText("84894654"), closeSoftKeyboard())
        onView(withId(R.id.btnContinue)).perform(click())
        onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
    }

    @Test
    fun enterTestSerialNo() {
        onView(withId(R.id.btnNext)).perform(click())
        onView(withId(R.id.etSerialNo))
            .perform(typeText("20110001"), closeSoftKeyboard())
        onView(withId(R.id.btnContinue)).perform(click())
        onView(withId(R.id.fragmentContainer)).check(matches(isDisplayed()))
    }
}