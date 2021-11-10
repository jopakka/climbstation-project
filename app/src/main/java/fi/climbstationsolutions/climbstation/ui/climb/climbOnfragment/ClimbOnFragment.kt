package fi.climbstationsolutions.climbstation.ui.climb.climbOnfragment

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
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbOnBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.ClimbActionActivity
import fi.climbstationsolutions.climbstation.utils.TimeService
import kotlin.math.roundToInt

class ClimbOnFragment : Fragment(R.layout.fragment_climb_on) {
    private lateinit var binding: FragmentClimbOnBinding
    private val args: ClimbOnFragmentArgs by navArgs()
    private val viewModel: ClimbOnViewModel by viewModels {
        ClimbOnViewModelFactory(requireContext())
    }

    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbOnBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        serviceIntent = Intent(context, TimeService::class.java)
        requireActivity().registerReceiver(updateTime, IntentFilter(TimeService.TIMER_UPDATED))

        binding.stopBtn.setOnClickListener {
            stopClimbing()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startClimbing()
    }


    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            time = p1.getDoubleExtra(TimeService.TIME_EXTRA, 0.0)
            binding.stopWatch.text = getTimeStringFromDouble(time)
        }

    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()

        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600

        return getString(R.string.stop_watch, hours, minutes, seconds)
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

        serviceIntent.putExtra(TimeService.TIME_EXTRA, time)
        activity.startService(serviceIntent)
    }

    private fun stopClimbing() {
        val context = context ?: return
        val activity = activity ?: return

        Intent(context, ClimbStationService::class.java).also {
            it.action = ClimbActionActivity.ACTION_STOP
            activity.startForegroundService(it)
        }

        activity.stopService(serviceIntent)
    }
}