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
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SerialFragmentTests {

    @Test
    fun serialFragmentVisible() {
        launchFragmentInContainer<SerialFragment>()
        onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
    }

    @Test
    fun continueIsVisible() {
        launchFragmentInContainer<SerialFragment>()
        onView(withId(R.id.etSerialNo)).perform(
            typeText("1231313"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.etSerialNo)).check(matches(isEnabled()))
    }

    @Test
    fun useCorrectTestSerialNumber() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val scenario = launchFragmentInContainer<SerialFragment>()
        scenario.onFragment {
            navController.setGraph(R.navigation.init_nav_graph)

            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.etSerialNo)).perform(
            typeText("20110001"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btnContinue)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.mainActivity)
    }

    @Test
    fun useIncorrectTestSerialNumber() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val scenario = launchFragmentInContainer<SerialFragment>()
        scenario.onFragment {
            navController.setGraph(R.navigation.init_nav_graph)

            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.etSerialNo)).perform(
            typeText("123"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btnContinue)).perform(click())
        onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
    }
}