package fi.climbstationsolutions.climbstation.ui.climb.climbOnfragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbOnBinding
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity
import fi.climbstationsolutions.climbstation.utils.TimeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import kotlin.math.roundToInt

class ClimbOnFragment : Fragment(R.layout.fragment_climb_on) {
    private lateinit var binding: FragmentClimbOnBinding
    private val args: ClimbOnFragmentArgs by navArgs()
    private val viewModel: ClimbOnViewModel by viewModels {
        ClimbOnViewModelFactory(requireContext())
    }

    private lateinit var broadcastManager: LocalBroadcastManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbOnBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        broadcastManager = LocalBroadcastManager.getInstance(requireContext()).apply {
            registerReceiver(broadcastReceiver, IntentFilter(ClimbStationService.BROADCAST_ID_NAME))
        }

        binding.stopBtn.setOnClickListener {
            stopClimbing()
        }

        if (ClimbStationService.SERVICE_RUNNING) {
            viewModel.startTimer()
            viewModel.serviceIsRunning()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!ClimbStationService.SERVICE_RUNNING)
            startClimbing()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra("id", 0L)
            if (id != 0L) {
                viewModel.getSessionId(id)
            }
            Log.d("ID", id.toString())
            viewModel.startTimer()
        }
    }

    private fun startClimbing() {

        val context = context ?: return
        val activity = activity ?: return
        val serial = PreferenceHelper.customPrefs(context, PREF_NAME)[SERIAL_NO_PREF_NAME, ""]

        Intent(context, ClimbStationService::class.java).also {
            it.putExtra(ClimbActionActivity.CLIMB_STATION_SERIAL_EXTRA, serial)
            it.putExtra(
                ClimbActionActivity.PROFILE_EXTRA,
                args.profile
            )
            activity.startForegroundService(it)
        }
    }

    private fun stopClimbing() {
        val context = context ?: return
        val activity = activity ?: return

        if (ClimbStationService.SERVICE_RUNNING) {
            Intent(context, ClimbStationService::class.java).also {
                it.action = ClimbActionActivity.ACTION_STOP
                activity.startForegroundService(it)
            }
        }

        val id = viewModel.sessionWithData.value?.session?.id
        val action =
            id?.let { ClimbOnFragmentDirections.actionClimbOnFragmentToClimbFinishedFragment(it) }
        if (action != null) {
            this.findNavController().navigate(action)
        }
    }
}