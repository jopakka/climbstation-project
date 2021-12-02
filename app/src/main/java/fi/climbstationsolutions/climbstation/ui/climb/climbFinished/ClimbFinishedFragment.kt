package fi.climbstationsolutions.climbstation.ui.climb.climbFinished

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME

class ClimbFinishedFragment : Fragment() {
    private lateinit var binding: FragmentClimbFinishedBinding
    private val viewModel: ClimbFinishedViewModel by viewModels()
    private val args: ClimbFinishedFragmentArgs by navArgs()
    private lateinit var broadcastManager: LocalBroadcastManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbFinishedBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.getSessionId(args.sessionId)
        viewModel.setProfile(args.climbProfile)

        setUI()
        setBroadcastManager()


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    private fun setBroadcastManager() {
        broadcastManager = LocalBroadcastManager.getInstance(requireContext()).apply {
            registerReceiver(broadcastReceiver, IntentFilter(ClimbStationService.BROADCAST_ID_NAME))
        }
    }

    private fun setUI() {
        binding.apply {
            btnClimbAgain.setOnClickListener(climbAgainAction)
            btnFinish.setOnClickListener(finishAction)
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra("id", -1L)
            viewModel.setLoading(false)
            if (id != -1L) {
                // Navigate to new fragment
                viewModel.profileWithSteps.value?.let {
                    val startAction = ClimbFinishedFragmentDirections.actionClimbFinishedFragmentToClimbOnFragment(it)
                    findNavController().navigate(startAction)
                }
            }
        }
    }

    private fun startClimbing() {

        val context = context ?: return
        val activity = activity ?: return
        val serial = PreferenceHelper.customPrefs(context, PREF_NAME)[SERIAL_NO_PREF_NAME, ""]
        val profile = viewModel.profileWithSteps.value ?: return
        Log.d("profile","profile: $profile")

        Intent(context, ClimbStationService::class.java).also {
            it.putExtra(ClimbStationService.CLIMB_STATION_SERIAL_EXTRA, serial)
            it.putExtra(
                ClimbStationService.PROFILE_EXTRA,
                profile
            )
            activity.startForegroundService(it)
        }
    }

    private val climbAgainAction = View.OnClickListener {
        Log.d("climbagain","here")
        viewModel.setLoading(true)
        startClimbing()
    }

    private val finishAction = View.OnClickListener {
        val direction = ClimbFinishedFragmentDirections.actionClimbFinishedFragmentToClimb()
        findNavController().navigate(direction)
    }
}