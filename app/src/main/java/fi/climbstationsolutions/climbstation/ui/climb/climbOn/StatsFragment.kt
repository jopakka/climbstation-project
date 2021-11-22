package fi.climbstationsolutions.climbstation.ui.climb.climbOn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import fi.climbstationsolutions.climbstation.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding
    private val climbOnViewModel: ClimbOnViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(layoutInflater)
        binding.viewModel = climbOnViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}