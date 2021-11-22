package fi.climbstationsolutions.climbstation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.ProfileTabPagerAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupPager()

        return binding.root
    }

    private fun setupPager() {
        binding.profilePager.adapter = ProfileTabPagerAdapter(this)
        TabLayoutMediator(binding.profileTab, binding.profilePager) { tab, pos ->
            tab.text = when (pos) {
                0 -> getString(R.string.stats)
                1 -> getString(R.string.history)
                else -> null
            }
        }.attach()
    }
}