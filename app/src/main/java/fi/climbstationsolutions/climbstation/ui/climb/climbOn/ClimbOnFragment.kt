package fi.climbstationsolutions.climbstation.ui.climb.climbOn

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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

class ClimbOnFragment : Fragment(R.layout.fragment_climb_on) {
    private lateinit var binding: FragmentClimbOnBinding
//    private val args: ClimbOnFragmentArgs by navArgs()
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
            registerReceiver(errorBroadcastReceiver, IntentFilter(ClimbStationService.BROADCAST_ERROR_CLIMB))
        }

        binding.stopBtn.setOnClickListener {
            stopClimbing()
        }

//        viewModel.getLastSession()
        viewModel.getProfile()
        viewModel.startTimer()

        setBackButtonAction()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(errorBroadcastReceiver)
    }

    private fun setBackButtonAction() {
        activity?.let {
            it.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                showYesNoDialog {
                    if(isEnabled) {
                        isEnabled = false
                        stopClimbing()
                    }
                }
            }
        }
    }

    private fun showYesNoDialog(positiveAction: () -> Unit) {
        val builder = AlertDialog.Builder(activity).apply {
            setTitle(R.string.error)
            setMessage(R.string.prompt_quit_session)
            setPositiveButton(R.string.yes) { _, _ ->
                positiveAction()
            }
            setNegativeButton(R.string.no) { d, _ ->
                d.cancel()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorDialog(message: String, positiveAction: () -> Unit) {
        val builder = AlertDialog.Builder(activity).apply {
            setTitle(R.string.error)
            setMessage(message)
            setPositiveButton(android.R.string.ok) { _, _ ->
                positiveAction()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private val errorBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra(ClimbStationService.EXTRA_ERROR) ?: return
            showErrorDialog(message) {
                findNavController().navigateUp()
            }
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