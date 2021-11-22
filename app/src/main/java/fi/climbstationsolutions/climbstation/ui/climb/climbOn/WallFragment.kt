package fi.climbstationsolutions.climbstation.ui.climb.climbOn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import fi.climbstationsolutions.climbstation.databinding.FragmentWallBinding

class WallFragment : Fragment() {
    private lateinit var binding: FragmentWallBinding
    private val climbOnViewModel: ClimbOnViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWallBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        val wall = binding.wall
        climbOnViewModel.sessionWithData.observe(viewLifecycleOwner) {
            wall.sessionWithData = it
        }
        climbOnViewModel.profileWithSteps.observe(viewLifecycleOwner) {
            wall.profile = it
        }
    }
}