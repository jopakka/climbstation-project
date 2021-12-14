package fi.climbstationsolutions.climbstation.ui.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import fi.climbstationsolutions.climbstation.ui.profile.history.ProfileHistoryFragment
import fi.climbstationsolutions.climbstation.ui.profile.stats.ProfileStatsFragment

/**
 * ViewPagerAdapter for profile fragment, where is displayed stats
 * and previous sessions.
 */
class ProfileTabPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> ProfileStatsFragment()
            1 -> ProfileHistoryFragment()
            else -> throw IllegalArgumentException("Incorrect position")
        }
        return fragment
    }

}