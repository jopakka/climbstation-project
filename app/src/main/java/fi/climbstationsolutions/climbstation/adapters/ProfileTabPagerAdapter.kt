package fi.climbstationsolutions.climbstation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import fi.climbstationsolutions.climbstation.ui.profile.ProfileHistoryFragment
import fi.climbstationsolutions.climbstation.ui.profile.ProfileStatsFragment

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