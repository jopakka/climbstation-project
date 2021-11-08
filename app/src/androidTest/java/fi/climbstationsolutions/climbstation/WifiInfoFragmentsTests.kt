package fi.climbstationsolutions.climbstation

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import fi.climbstationsolutions.climbstation.ui.init.WifiInfoFragment
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WifiInfoFragmentsTests {

    @Test
    fun wifiInfoFragmentVisible() {
        launchFragmentInContainer<WifiInfoFragment>()
        onView(withId(R.id.btnNext)).check(matches(isDisplayed()))
    }

    @Test
    fun nextButtonWork() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val scenario = launchFragmentInContainer<WifiInfoFragment>()
        scenario.onFragment {
            navController.setGraph(R.navigation.init_nav_graph)

            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.btnNext)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.serialFragment)
    }
}