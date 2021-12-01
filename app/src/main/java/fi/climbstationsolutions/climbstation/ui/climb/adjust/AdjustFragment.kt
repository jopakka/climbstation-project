package fi.climbstationsolutions.climbstation.ui.climb.adjust

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
import fi.climbstationsolutions.climbstation.databinding.FragmentAdjustBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.viewmodels.AdjustViewModel
import me.angrybyte.numberpicker.listener.OnValueChangeListener

class
AdjustFragment : Fragment(), NumberPicker.OnValueChangeListener, OnValueChangeListener {
    private lateinit var binding: FragmentAdjustBinding
    private lateinit var broadcastManager: LocalBroadcastManager
    private val TAG: String = AdjustFragment::class.java.simpleName

    private val viewModel: AdjustViewModel by viewModels()

    private var angleListWidth: Int? = null
    private var lengthListWidth: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdjustBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.setLoading(ClimbStationService.SERVICE_RUNNING)

        binding.adjustFragmentTimepickerMinute.minValue = 0
        binding.adjustFragmentTimepickerSecond.minValue = 0
        binding.adjustFragmentTimepickerMinute.maxValue = 60
        binding.adjustFragmentTimepickerSecond.maxValue = 60

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
            viewModel.setLoading(false)
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
                        AdjustFragmentDirections.actionAdjustFragmentToClimbOnFragment()
                    findNavController().navigate(startAction)
                }
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeVariables()
        initializeSelectedLength()
        initializeSelectedAngle()

        Log.d("SF1", "selected values: ${viewModel.getClimbProfileWithSteps()}")

        binding.adjustFragmentAnglePicker.setListener(this)
        binding.adjustFragmentLengthPicker.setListener(this)
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
        binding.adjustFragmentTimepickerMinute.minValue = 0
        binding.adjustFragmentTimepickerSecond.minValue = 0
        binding.adjustFragmentTimepickerMinute.maxValue = 60
        binding.adjustFragmentTimepickerSecond.maxValue = 60

        binding.adjustFragmentTimepickerMinute.setFormatter { i: Int ->
            String.format("%02d", i)
        }
        binding.adjustFragmentTimepickerSecond.setFormatter { i: Int ->
            String.format("%02d", i)
        }
        binding.adjustFragmentTimepickerMinute.setOnValueChangedListener(this)
        binding.adjustFragmentTimepickerSecond.setOnValueChangedListener(this)

        angleListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.string_list_layout_width)
        lengthListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.number_list_layout_width)
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        when (val tag = p0?.tag) {
            "minute" -> {
                viewModel.setMinute(p2)
            }
            "second" -> {
                viewModel.setSecond(p2)
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
        val profile = viewModel.profileWithSteps.value ?: return
        val timer = viewModel.getTime()

        Intent(context, ClimbStationService::class.java).also {
            it.putExtra(ClimbStationService.CLIMB_STATION_SERIAL_EXTRA, serial)
            it.putExtra(ClimbStationService.PROFILE_EXTRA, profile)
            it.putExtra(ClimbStationService.TIMER_EXTRA, timer)
            activity.startForegroundService(it)
        }
    }

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.adjustFragmentStartBtn -> {
                Log.d("STARTBTN", "Works")
                viewModel.setLoading(true)
                viewModel.setClimbProfileWithSteps()
                viewModel.getTime()
                startClimbing()
            }
        }
    }

    override fun onValueChanged(oldValue: Int, newValue: Int) {
        Log.d(TAG, "Currently the picker is at $newValue degrees. oldValue: $oldValue")
        Log.d(TAG, "anglepicker value: ${binding.adjustFragmentAnglePicker.value}, newValue: $newValue")
        Log.d(TAG, "lengthpicker value: ${binding.adjustFragmentLengthPicker.value}, newValue: $newValue")
        viewModel.setAngle(binding.adjustFragmentAnglePicker.value)
        viewModel.setLength(binding.adjustFragmentLengthPicker.value)
    }
}