package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithDataDao
import fi.climbstationsolutions.climbstation.database.SettingsDao
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding
import fi.climbstationsolutions.climbstation.ui.climb.climbOnfragment.ClimbOnFragmentArgs
import fi.climbstationsolutions.climbstation.ui.viewmodels.ClimbFinishedViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.ClimbFinishedViewModelFactory
import fi.climbstationsolutions.climbstation.utils.CalorieCounter
import kotlinx.coroutines.*

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

        viewModel.addSessionId(args.sessionId)
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