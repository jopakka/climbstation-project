package fi.climbstationsolutions.climbstation.ui.climb

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import fi.climbstationsolutions.climbstation.ui.climb.climbSetup.ClimbProfilesFragment
import fi.climbstationsolutions.climbstation.ui.climb.manualStart.ManualStartFragment

/**
 * ViewPagerAdapter for climb fragment, where is displayed climbProfiles
 * and manual start.
 */
class ClimbTabPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> ClimbProfilesFragment()
            1 -> ManualStartFragment()
            else -> throw IllegalArgumentException("Incorrect position")
        }
        return fragment
    }

}