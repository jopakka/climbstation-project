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
//            val preferencePos = getPref()

            binding.difficultyRv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = DifficultyRecyclerviewAdapter(this@ClimbFragment)
            }

            setProfilesToRecyclerView()

            binding.startBtn.setOnClickListener(clickListener)

            // currently inactive, as proper functionality has not been yet implemented
            binding.adjustBtn.setOnClickListener(clickListener)
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
            setProfile(it.first())
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

//    private fun savePref() {
//        val preferences =
//            activity?.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE) ?: return
//        val editor = preferences.edit()
//
//        viewModel.profile.value?.let {
//            editor.putString("PROFILE_LEVEL", it.level.toString())
//        }
//        editor.apply()
//    }
//
//    private fun getPref(): Int? {
//        val preferences =
//            activity?.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE) ?: return null
//        val level: String? = preferences.getString("PROFILE_LEVEL", null)
//
//        val profiles = ProfileHandler.readProfiles(context ?: return null, R.raw.profiles)
//        var prefPosition = 0
//        if (level == null) {
//            viewModel.postValue(profiles.first())
//        } else {
//            for (profile in profiles) {
//                if (profile.level == level.toInt()) {
//                    viewModel.postValue(profile)
//                    prefPosition = profiles.indexOf(profile)
//                }
//            }
//        }
//        return prefPosition
//    }

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.adjustBtn -> {
//                val adjustAction = ClimbFragmentDirections.actionClimbToAdjustFragment()
//                this.findNavController().navigate(adjustAction)

                val text = "Not yet implemented"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(requireContext(), text, duration)
                toast.show()
            }
            binding.startBtn -> {
                Log.d("STARTBTN", "Works")
                val profile = viewModel.profileWithSteps.value ?: return@OnClickListener
                val startAction = ClimbFragmentDirections.actionClimbToClimbOnFragment(profile)
                this.findNavController().navigate(startAction)
            }
        }
    }
}