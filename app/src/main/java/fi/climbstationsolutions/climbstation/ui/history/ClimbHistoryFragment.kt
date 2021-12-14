package fi.climbstationsolutions.climbstation.ui.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.databinding.FragmentHistoryBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.climb.climbFinished.ClimbFinishedFragmentDirections

class ClimbHistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: ClimbHistoryViewModel by viewModels()
    private val args: ClimbHistoryFragmentArgs by navArgs()
    private var broadcastManager: LocalBroadcastManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getSession(args.sessionId)

        binding.button.setOnClickListener(climbAgainAction)

        setBroadcastManager()

        return binding.root
    }

    private fun startClimbing() {

        val context = context ?: return
        val activity = activity ?: return
        val serial = PreferenceHelper.customPrefs(context, PREF_NAME)[SERIAL_NO_PREF_NAME, ""]
        val profile = viewModel.profileWithSteps.value ?: return

        Intent(context, ClimbStationService::class.java).also {
            it.putExtra(ClimbStationService.CLIMB_STATION_SERIAL_EXTRA, serial)
            it.putExtra(
                ClimbStationService.PROFILE_EXTRA,
                profile
            )
            activity.startForegroundService(it)
        }
    }

    private fun setBroadcastManager() {
        context?.let {
            broadcastManager = LocalBroadcastManager.getInstance(it).apply {
                registerReceiver(broadcastReceiver, IntentFilter(ClimbStationService.BROADCAST_ID_NAME))
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra("id", -1L)
            viewModel.setLoading(false)
            if (id != -1L) {
                // Navigate to new fragment
                viewModel.profileWithSteps.value?.let {
                    val startAction = ClimbHistoryFragmentDirections.actionClimbHistoryToClimbOnFragment(it)
                    findNavController().navigate(startAction)
                }
            }
        }
    }

    private val climbAgainAction = View.OnClickListener {
        viewModel.setLoading(true)
        startClimbing()
    }
}