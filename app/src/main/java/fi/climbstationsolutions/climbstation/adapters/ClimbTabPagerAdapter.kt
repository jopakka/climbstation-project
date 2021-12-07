package fi.climbstationsolutions.climbstation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import fi.climbstationsolutions.climbstation.ui.climb.ClimbProfilesFragment
import fi.climbstationsolutions.climbstation.ui.climb.manualStart.ManualStartFragment

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