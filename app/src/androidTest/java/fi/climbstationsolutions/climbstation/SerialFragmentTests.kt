package fi.climbstationsolutions.climbstation

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import fi.climbstationsolutions.climbstation.ui.init.SerialFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SerialFragmentTests {

    // For some reason this doesn't work on github actions anymore.
    // Locally it still works
    @Test
    fun serialFragmentVisible() {
        launchFragmentInContainer<SerialFragment>()
        onView(withId(R.id.viewFinder)).check(matches(isDisplayed()))
    }
}
