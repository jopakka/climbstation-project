package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.ClimbTabPagerAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbBinding

class ClimbFragment : Fragment() {
    private lateinit var binding: FragmentClimbBinding
    private val viewModel: ClimbViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupPager()

        return binding.root
    }

    private fun setupPager() {
        binding.climbPager.adapter = ClimbTabPagerAdapter(this)
        TabLayoutMediator(binding.climbTabLayout, binding.climbPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> getString(R.string.profiles)
                1 -> getString(R.string.manual)
                else -> null
            }
        }.attach()
    }
}