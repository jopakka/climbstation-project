package fi.climbstationsolutions.climbstation.ui.climb.manualStart

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
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentManualStartBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.climb.ClimbFragmentDirections
import fi.climbstationsolutions.climbstation.ui.climb.ClimbViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.ManualStartViewModel
import me.angrybyte.numberpicker.listener.OnValueChangeListener
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper

import androidx.recyclerview.widget.SnapHelper

import fi.climbstationsolutions.climbstation.adapters.TestiAdapter

import android.widget.TextView
import android.widget.Toast

import fi.climbstationsolutions.climbstation.ui.MainActivity

import android.R.attr.name
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.*
import androidx.core.view.setPadding
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager.onScrollStopListener


class ManualStartFragment : Fragment(), NumberPicker.OnValueChangeListener {
    private lateinit var binding: FragmentManualStartBinding
    private lateinit var broadcastManager: LocalBroadcastManager
    private val TAG: String = ManualStartFragment::class.java.simpleName

    private val viewModel: ManualStartViewModel by viewModels()
    private val climbViewModel: ClimbViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private var angleListWidth: Int? = null
    private var lengthListWidth: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManualStartBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        climbViewModel.setLoading(ClimbStationService.SERVICE_RUNNING)

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
        broadcastManager.unregisterReceiver(broadcastReceiver)
        broadcastManager.unregisterReceiver(errorsBroadcastReceiver)
    }

    private val errorsBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val error = intent.getStringExtra(ClimbStationService.EXTRA_ERROR) ?: return
            showAlertDialog(error)
            climbViewModel.setLoading(false)
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra("id", -1L)
            climbViewModel.setLoading(false)
            if (id != -1L) {
                // Navigate to new fragment
                viewModel.profileWithSteps.value?.let {
                    val startAction = ClimbFragmentDirections.actionClimbToClimbOnFragment(
                        it,
                        viewModel.getTime() ?: -1
                    )
                    findNavController().navigate(startAction)
                }
            }
        }
    }

    private fun showAlertDialog(message: String) {
        activity?.let {
            val builder = AlertDialog.Builder(it).apply {
                setTitle(R.string.error)
                setMessage(message + getString(R.string.error_connect))
                setPositiveButton(android.R.string.ok) { d, _ ->
                    d.cancel()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeSelectedLength()
        initializeSelectedAngle()
        initializeVariables()

        Log.d("SF1", "selected values: ${viewModel.getClimbProfileWithSteps()}")

        binding.adjustFragmentStartBtn.setOnClickListener(clickListener)

    }

    private fun initializeSelectedAngle() {
        if (viewModel.getClimbProfileWithSteps()?.steps?.get(0)?.angle == null) {
            val currentAngle = viewModel.angleNumbers[1]
            viewModel.setAngle(currentAngle)
        }
    }

    private fun initializeSelectedLength() {
        if (viewModel.getClimbProfileWithSteps()?.steps?.get(0)?.distance == null) {
            val currentLength = viewModel.lengthNumbers[10]
            viewModel.setLength(currentLength)
        }
    }

    private fun initializeVariables() {
        binding.adjustFragmentTimepickerMinute.apply {
            minValue = 0
            maxValue = 60
            setFormatter { i: Int -> String.format("%02d", i) }
            setOnValueChangedListener(this@ManualStartFragment)
            value = viewModel.timer.value?.minute ?: 0
        }

        binding.adjustFragmentTimepickerSecond.apply {
            minValue = 0
            maxValue = 59
            setFormatter { i: Int -> String.format("%02d", i) }
            setOnValueChangedListener(this@ManualStartFragment)
            value = viewModel.timer.value?.second ?: 0
        }

        binding.pickerLength.apply {
            minValue = 0
            maxValue = 1000
            setOnValueChangedListener(this@ManualStartFragment)
        }
        binding.pickerAngle.apply {
            minValue = 0
            maxValue = 60
            val list = (15 downTo -45).toList().map {
                it.toString()
            }.toTypedArray()
            displayedValues = list
            value = 15
            setOnValueChangedListener(this@ManualStartFragment)
        }
        angleListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.string_list_layout_width)
        lengthListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.number_list_layout_width)
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        when (p0?.id) {
            binding.adjustFragmentTimepickerMinute.id -> {
                viewModel.setMinute(p2)
            }
            binding.adjustFragmentTimepickerSecond.id -> {
                viewModel.setSecond(p2)
            }
            binding.pickerLength.id -> {
                viewModel.setLength(p2)
            }
            binding.pickerAngle.id -> {
                viewModel.setAngle(15 - p2)
            }
            else -> {
                Log.d("NumberPicker_onValueChange", "no NumberPicker with tag: $tag found")
            }
        }
    }

    private fun startClimbing() {
        val context = context ?: return
        val activity = activity ?: return
        val serial = PreferenceHelper.customPrefs(context, PREF_NAME)[SERIAL_NO_PREF_NAME, ""]

        viewModel.profileWithSteps.observe(viewLifecycleOwner) { profile ->
            if (profile == null) return@observe
            val timer = viewModel.getTime()
            Log.d("profileadjust", "profile: $profile")

            Intent(context, ClimbStationService::class.java).also {
                it.putExtra(ClimbStationService.CLIMB_STATION_SERIAL_EXTRA, serial)
                it.putExtra(ClimbStationService.PROFILE_EXTRA, profile)
                it.putExtra(ClimbStationService.TIMER_EXTRA, timer)
                activity.startForegroundService(it)
            }
        }
    }

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.adjustFragmentStartBtn -> {
                Log.d("STARTBTN", "Works")
                climbViewModel.setLoading(true)
                viewModel.setClimbProfileWithSteps()
                viewModel.getTime()
                startClimbing()
            }
        }
    }
}