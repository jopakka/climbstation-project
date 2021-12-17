package fi.climbstationsolutions.climbstation.ui.history

import android.app.AlertDialog
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
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentHistoryBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
/**
 * @author Patrik Pölkki
 * @author Joonas Niemi
 * Fragment shows how selected climb session has went
 * User can also start chosen climb session again if
 * its not manual session or deleted custom session
 */
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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            broadcastManager = LocalBroadcastManager.getInstance(it).apply {
                registerReceiver(
                    broadcastReceiver,
                    IntentFilter(ClimbStationService.BROADCAST_ID_NAME)
                )
                registerReceiver(
                    errorsBroadcastReceiver,
                    IntentFilter(ClimbStationService.BROADCAST_ERROR)
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        broadcastManager?.unregisterReceiver(broadcastReceiver)
        broadcastManager?.unregisterReceiver(errorsBroadcastReceiver)
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

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra("id", -1L)
            viewModel.setLoading(false)
            if (id != -1L) {
                // Navigate to new fragment
                viewModel.profileWithSteps.value?.let {
                    val startAction =
                        ClimbHistoryFragmentDirections.actionClimbHistoryToClimbOnFragment(it)
                    findNavController().navigate(startAction)
                }
            }
        }
    }

    private val errorsBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val error = intent.getStringExtra(ClimbStationService.EXTRA_ERROR) ?: return
            showAlertDialog(error)
            viewModel.setLoading(false)
        }
    }

    private fun showAlertDialog(message: String) {
        activity?.let {
            val builder = AlertDialog.Builder(it).apply {
                setTitle(R.string.error)
                setMessage(message)
                setPositiveButton(android.R.string.ok) { d, _ ->
                    d.cancel()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private val climbAgainAction = View.OnClickListener {
        viewModel.setLoading(true)
        startClimbing()
    }
}