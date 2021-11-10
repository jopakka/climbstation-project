package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithDataDao
import fi.climbstationsolutions.climbstation.database.SettingsDao
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding
import fi.climbstationsolutions.climbstation.ui.viewmodels.ClimbFinishedViewModel
import fi.climbstationsolutions.climbstation.utils.CalorieCounter
import kotlinx.coroutines.*

class ClimbFinishedFragment : Fragment() {
    private lateinit var binding: FragmentClimbFinishedBinding
    private val viewModel: ClimbFinishedViewModel by viewModels()

    private val parentJob = Job()
    private val mainScope = CoroutineScope(Dispatchers.Main + parentJob)

    private var sessionId: Long? = null

    private lateinit var sessionWithDataDao: SessionWithDataDao
    private lateinit var settingsDao: SettingsDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbFinishedBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initValues(view)

        settingsDao = AppDatabase.get(requireContext()).settingsDao()

        // gets session id of this session
        // not sure if this crashes when called from climb fragment
        sessionId = arguments?.getLong("sessionId")
        Log.d("CFF", "args: ${arguments}")

        // call this when climbing session ends
        if (sessionId == 0L) {
            insertValuesToUI()
        }

        // call this when this fragment is used to display session from statistics
        else {
            insertValuesToUIFromDatabase(sessionId!!)
        }
    }

    private fun initValues(view: View) {
        binding.viewEarlierResultsBtn.setOnClickListener(clickListener)
    }

    // Inserts values fetched from database to the corresponding UI elements
    // Currently uses dummy values. Eventually values will be fetched from database.
    private fun insertValuesToUI() {
        val title = "Congratulations!"
        val actualLength = 18.3f
        val goalLength = 20f
        val startDifficulty = "Beginner"
        val endDifficulty = "Athlete"
        val mode = "To next difficulty"
        val time = "00:14:22"
        val length = 18.3f
        val calories = 140.6f
        val speed = 8.7f

        viewModel.setSelectedTitle(title)
        viewModel.setSelectedTitleLength(actualLength)
        viewModel.setSelectedTitleGoalLength(goalLength)
        viewModel.setSelectedDifficultyStart(startDifficulty)
        viewModel.setSelectedDifficultyEnd(endDifficulty)
        viewModel.setSelectedMode(mode)
        viewModel.setDuration(time)
        viewModel.setDistance(length)
        viewModel.setCalories(calories)
        viewModel.setAverageSpeed(speed)
    }

    private fun insertValuesToUIFromDatabase(sessionId: Long) {
        // fetch all necessary items from database
        mainScope.launch {
            val session = getSession(sessionId)
            val date = session.session.createdAt
            val actualLength = session.data.last().totalDistance.toFloat()
            val goalLength = 20f
            val startDifficulty = "Beginner"
            val endDifficulty = "Athlete"
            val mode = "To next difficulty"
            val duration = "00:05:17"
            val userWeight = settingsDao.getBodyWeightById(1)?.weight
            val calories = userWeight?.let { CalorieCounter().countCalories(actualLength, it) }

            val speedsArray = mutableListOf<Int>()
            for (item in session.data.indices) {
                speedsArray.add(session.data[item].speed)
            }

            val averageSpeed = speedsArray.average().toFloat()

            viewModel.setSelectedTitle(date.toString())
            viewModel.setSelectedTitleLength(actualLength)
            viewModel.setSelectedTitleGoalLength(goalLength)
            viewModel.setSelectedDifficultyStart(startDifficulty)
            viewModel.setSelectedDifficultyEnd(endDifficulty)
            viewModel.setSelectedMode(mode)
            viewModel.setDuration(duration)
            viewModel.setDistance(actualLength)
            viewModel.setCalories(calories ?: 0f)
            viewModel.setAverageSpeed(averageSpeed)
        }
    }

    private suspend fun getSession(args: Long) = withContext(Dispatchers.IO) {
        sessionWithDataDao = AppDatabase.get(requireContext()).sessionDao()
        val getSession = sessionWithDataDao.getSessionWithData(args)
        Log.d("statistics_fragment", "${getSession}")
        return@withContext getSession
    }

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.viewEarlierResultsBtn -> {
                val action =
                    ClimbFinishedFragmentDirections.actionClimbFinishedFragmentToStatistic()
                this.findNavController().navigate(action)
            }
        }
    }
}