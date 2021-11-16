package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.adapters.DifficultyRecyclerviewAdapter
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService

class ClimbFragment : Fragment(), CellClickListener {
    private lateinit var binding: FragmentClimbBinding

    private val viewModel: ClimbViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        if (!isServiceRunning()) {
            binding.difficultyRv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = DifficultyRecyclerviewAdapter(this@ClimbFragment)
            }

            setProfilesToRecyclerView()

            binding.startBtn.setOnClickListener(clickListener)
        }
        return binding.root
    }

    override fun onCellClickListener(profile: ClimbProfileWithSteps) {
        setProfile(profile)
    }

    private fun setProfilesToRecyclerView() {
        viewModel.allProfiles.observe(viewLifecycleOwner) {
            val adapter = binding.difficultyRv.adapter as DifficultyRecyclerviewAdapter
            adapter.addProfiles(it)

            val prof = it.firstOrNull()
            if(prof != null) {
                setProfile(prof)
            }
        }
    }

    private fun setProfile(profile: ClimbProfileWithSteps) {
        viewModel.setProfile(profile)
    }

    private fun isServiceRunning(): Boolean {
        return if (ClimbStationService.SERVICE_RUNNING) {
            val startAction = ClimbFragmentDirections.actionClimbToClimbOnFragment(null)
            this.findNavController().navigate(startAction)
            true
        } else {
            false
        }
    }

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.startBtn -> {
                Log.d("STARTBTN", "Works")
                val profile = viewModel.profileWithSteps.value ?: return@OnClickListener
                val startAction = ClimbFragmentDirections.actionClimbToClimbOnFragment(profile)
                this.findNavController().navigate(startAction)
            }
        }
    }
}