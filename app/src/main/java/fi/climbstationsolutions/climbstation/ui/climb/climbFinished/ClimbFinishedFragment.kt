package fi.climbstationsolutions.climbstation.ui.climb.climbFinished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding

class ClimbFinishedFragment : Fragment() {
    private lateinit var binding: FragmentClimbFinishedBinding
    private val viewModel: ClimbFinishedViewModel by viewModels()
    private val args: ClimbFinishedFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbFinishedBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.getSessionId(args.sessionId)
        viewModel.getProfile()

        setWallProfile()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setWallProfile() {
        viewModel.profileWithSteps.observe(viewLifecycleOwner) {
            binding.climbFinishedWallProfile.profile = it
        }

        viewModel.sessionWithData.observe(viewLifecycleOwner) {
            binding.climbFinishedWallProfile.sessionWithData = it
        }
    }
}