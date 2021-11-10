package fi.climbstationsolutions.climbstation.ui.climb

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.DifficultyRecyclerviewAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbBinding
import fi.climbstationsolutions.climbstation.network.profile.Profile
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler

class ClimbFragment : Fragment(), CellClicklistener {
    private lateinit var binding: FragmentClimbBinding

    private val viewModel: ClimbViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val preferencePos = getPref()

        binding.difficultyRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            adapter = DifficultyRecyclerviewAdapter(this@ClimbFragment, context, preferencePos)
        }

        binding.startBtn.setOnClickListener(clickListener)
        binding.adjustBtn.setOnClickListener(clickListener)

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        savePref()
    }

    override fun onCellClickListener(profile: Profile) {
        viewModel.postValue(profile)
    }

    private fun savePref() {
        val preferences =
            activity?.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE) ?: return
        val editor = preferences.edit()

        viewModel.profile.value?.let {
            editor.putString("PROFILE_LEVEL", it.level.toString())
        }
        editor.apply()
    }

    private fun getPref(): Int? {
        val preferences =
            activity?.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE) ?: return null
        val level: String? = preferences.getString("PROFILE_LEVEL", null)

        val profiles = ProfileHandler.readProfiles(context ?: return null, R.raw.profiles)
        var prefPosition = 0
        if (level == null) {
            viewModel.postValue(profiles.first())
        } else {
            for (profile in profiles) {
                if (profile.level == level.toInt()) {
                    viewModel.postValue(profile)
                    prefPosition = profiles.indexOf(profile)
                }
            }
        }
        return prefPosition
    }

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.adjustBtn -> {
                val adjustAction = ClimbFragmentDirections.actionClimbToAdjustFragment()
                this.findNavController().navigate(adjustAction)
            }
            binding.startBtn -> {
                Log.d("STARTBTN", "Works")
                val profile = viewModel.profile.value ?: return@OnClickListener
                val startAction = ClimbFragmentDirections.actionClimbToClimbOnFragment(profile)
                this.findNavController().navigate(startAction)
            }
        }
    }
}