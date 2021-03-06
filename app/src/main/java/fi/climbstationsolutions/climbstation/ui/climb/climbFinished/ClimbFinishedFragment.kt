package fi.climbstationsolutions.climbstation.ui.climb.climbFinished

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
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
/**
 * @author Joonas Niemi
 * Fragment shows how users climb session went
 */
class ClimbFinishedFragment : Fragment() {
    private lateinit var binding: FragmentClimbFinishedBinding
    private val viewModel: ClimbFinishedViewModel by viewModels()
    private val args: ClimbFinishedFragmentArgs by navArgs()
    private var broadcastManager: LocalBroadcastManager? = null

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
        broadcastManager?.unregisterReceiver(broadcastReceiver)
    }

    private fun setBroadcastManager() {
        context?.let {
            broadcastManager = LocalBroadcastManager.getInstance(it).apply {
                registerReceiver(
                    broadcastReceiver,
                    IntentFilter(ClimbStationService.BROADCAST_ID_NAME)
                )
            }
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
                    val startAction =
                        ClimbFinishedFragmentDirections.actionClimbFinishedFragmentToClimbOnFragment(
                            it
                        )
                    findNavController().navigate(startAction)
                }
            }
        }
    }

    private fun startClimbing() {

        val context = context ?: return
        val activity = activity ?: return
        val serial = PreferenceHelper.customPrefs(context, PREF_NAME)[SERIAL_NO_PREF_NAME, ""]

        viewModel.profileWithSteps.observe(viewLifecycleOwner) { profile ->
            if (profile == null) return@observe

            Intent(context, ClimbStationService::class.java).also {
                it.putExtra(ClimbStationService.CLIMB_STATION_SERIAL_EXTRA, serial)
                it.putExtra(
                    ClimbStationService.PROFILE_EXTRA,
                    profile
                )

                val timer = args.timer
                if (timer != -1) {
                    it.putExtra(ClimbStationService.TIMER_EXTRA, timer)
                }
                activity.startForegroundService(it)
            }
        }
    }

    private val climbAgainAction = View.OnClickListener {
        viewModel.setLoading(true)
        startClimbing()
    }

    private val finishAction = View.OnClickListener {
        findNavController().navigateUp()
    }
}