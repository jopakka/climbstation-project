package fi.climbstationsolutions.climbstation.ui.climb.climbSetup

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbProfilesBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.services.ClimbStationService.Companion.BROADCAST_ERROR
import fi.climbstationsolutions.climbstation.services.ClimbStationService.Companion.BROADCAST_ID_NAME
import fi.climbstationsolutions.climbstation.services.ClimbStationService.Companion.EXTRA_ERROR
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.MainActivityViewModel
/**
 * @author Patrik Pölkki
 * @author Joonas Niemi
 * Fragment for climbing using climb profiles
 */
class ClimbProfilesFragment : Fragment(), CellClickListener {
    private lateinit var binding: FragmentClimbProfilesBinding
    private var broadcastManager: LocalBroadcastManager? = null

    private val viewModel: ClimbViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private val mainViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbProfilesBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.mainViewModel = mainViewModel
        viewModel.setLoading(ClimbStationService.SERVICE_RUNNING)

        binding.difficultyRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DifficultyRecyclerviewAdapter(this@ClimbProfilesFragment)
        }

        setProfilesToRecyclerView()

        binding.startBtn.setOnClickListener(clickListener)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            broadcastManager = LocalBroadcastManager.getInstance(it).apply {
                registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_ID_NAME))
                registerReceiver(errorsBroadcastReceiver, IntentFilter(BROADCAST_ERROR))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        broadcastManager?.unregisterReceiver(broadcastReceiver)
        broadcastManager?.unregisterReceiver(errorsBroadcastReceiver)
    }

    override fun onCellClickListener(profile: ClimbProfileWithSteps) {
        setProfile(profile)
    }

    private fun setProfilesToRecyclerView() {
        viewModel.allProfiles.observe(viewLifecycleOwner) { list ->
            val adapter = binding.difficultyRv.adapter as DifficultyRecyclerviewAdapter
            adapter.addHeaderAndSubmitList(list) { id ->
                val prof = list.firstOrNull { it.profile.id == id }
                if (prof != null)
                    setProfile(prof)
            }
        }
    }

    private fun setProfile(profile: ClimbProfileWithSteps) {
        viewModel.setProfile(profile)
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

    private val errorsBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val error = intent.getStringExtra(EXTRA_ERROR) ?: return
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
                    val startAction = ClimbFragmentDirections.actionClimbToClimbOnFragment(it)
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

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.startBtn -> {
                Log.d("STARTBTN", "Works")
                viewModel.setLoading(true)
                startClimbing()
            }
        }
    }


}