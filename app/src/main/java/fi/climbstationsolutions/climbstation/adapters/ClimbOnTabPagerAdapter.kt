package fi.climbstationsolutions.climbstation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import fi.climbstationsolutions.climbstation.ui.climb.climbOn.StatsFragment
import fi.climbstationsolutions.climbstation.ui.climb.climbOn.WallFragment

/**
 * ViewPagerAdapter for climbOn fragment, where is displayed wall profile
 * and session statistics.
 */
class ClimbOnTabPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> WallFragment()
            1 -> StatsFragment()
            else -> throw IllegalArgumentException("Incorrect position")
        }
        return fragment
    }

}