package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding
import fi.climbstationsolutions.climbstation.ui.viewmodels.ClimbFinishedViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.ClimbFinishedViewModelFactory

class ClimbFinishedFragment : Fragment() {
    private lateinit var binding: FragmentClimbFinishedBinding
    private val viewModel: ClimbFinishedViewModel by viewModels {
        ClimbFinishedViewModelFactory(requireContext())
    }

    private val args: ClimbFinishedFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbFinishedBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.getSessionId(args.sessionId)
        binding.viewEarlierResultsBtn.setOnClickListener(clickListener)
        // Inflate the layout for this fragment
        return binding.root
    }

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.viewEarlierResultsBtn -> {
                val action =
                    ClimbFinishedFragmentDirections.actionClimbFinishedFragmentToStatistic()
                this.findNavController().navigate(action)
            }
        }
    }
}