package fi.climbstationsolutions.climbstation.ui.climb.climbOn

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbOnBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME

class ClimbOnFragment : Fragment(R.layout.fragment_climb_on) {
    private lateinit var binding: FragmentClimbOnBinding
//    private val args: ClimbOnFragmentArgs by navArgs()
    private val viewModel: ClimbOnViewModel by viewModels {
        ClimbOnViewModelFactory(requireContext())
    }

//    private lateinit var broadcastManager: LocalBroadcastManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbOnBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

//        broadcastManager = LocalBroadcastManager.getInstance(requireContext()).apply {
//            registerReceiver(broadcastReceiver, IntentFilter(ClimbStationService.BROADCAST_ID_NAME))
//        }

        binding.stopBtn.setOnClickListener {
            stopClimbing()
        }

            viewModel.startTimer()
            viewModel.getLastSession()

        setBackButtonAction()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    private fun setBackButtonAction() {
        activity?.let {
            it.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                showDialog {
                    if(isEnabled) {
                        isEnabled = false
                        stopClimbing()
                    }
                }
            }
        }
    }

    private fun showDialog(positiveAction: () -> Unit) {
        val builder = AlertDialog.Builder(activity).apply {
            setTitle(R.string.error)
            setMessage("Do you want to quit session?")
            setPositiveButton("Yes") { _, _ ->
                positiveAction()
            }
            setNegativeButton("No") { d, _ ->
                d.cancel()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra("id", 0L)
            if (id != 0L) {
                viewModel.getSessionById(id)
            } else {
                viewModel.getLastSession()
            }
            viewModel.getProfile()
            viewModel.startTimer()
        }
    }

    private fun stopClimbing() {
        val context = context ?: return
        val activity = activity ?: return

        if (ClimbStationService.SERVICE_RUNNING) {
            Intent(context, ClimbStationService::class.java).also {
                it.action = ClimbStationService.ACTION_STOP
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