package fi.climbstationsolutions.climbstation

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import fi.climbstationsolutions.climbstation.ui.init.SerialFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SerialFragmentTests {

    @Test
    fun serialFragmentVisible() {
        launchFragmentInContainer<SerialFragment>()
        onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
    }
}
