package fi.climbstationsolutions.climbstation.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fi.climbstationsolutions.climbstation.adapters.StatisticsAdapter
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.FragmentProfileBinding
import fi.climbstationsolutions.climbstation.ui.climb.CellClickListener


class ProfileFragment : Fragment(), SessionClickListener {
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

        viewModel.allSessions.observe(viewLifecycleOwner) {
            Log.d("RV", it.toString())
            binding.sessionRv.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                adapter = StatisticsAdapter(it, this@ProfileFragment)
            }
        }


        return binding.root
    }

    override fun onClick(sessionId: Long) {
        val direction = ProfileFragmentDirections.actionProfileToClimbHistory(sessionId)
        findNavController().navigate(direction)
    }
}