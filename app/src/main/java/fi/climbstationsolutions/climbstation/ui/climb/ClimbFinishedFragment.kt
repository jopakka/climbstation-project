package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.replace
import com.google.android.material.button.MaterialButton
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.database.SessionWithDataDao
import fi.climbstationsolutions.climbstation.database.SettingsDao
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsFragment
import fi.climbstationsolutions.climbstation.utils.CalorieCounter
import kotlinx.coroutines.*

class ClimbFinishedFragment() : Fragment() {
    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + parentJob)

    private lateinit var sessionWithDataDao: SessionWithDataDao
    private lateinit var settingsDao: SettingsDao

    private lateinit var sessionsList: List<SessionWithData>

    private var args: Long? = null

    private lateinit var toStatisticsBtn: MaterialButton
    private lateinit var resultTitle: TextView
    private lateinit var resultDetail: TextView
    private lateinit var resultStartDifficulty: TextView
    private lateinit var resultEndDifficulty: TextView
    private lateinit var resultMode: TextView
    private lateinit var resultTime: TextView
    private lateinit var resultLength: TextView
    private lateinit var resultCalories: TextView
    private lateinit var resultSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_climb_finished, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initValues(view)

        settingsDao = AppDatabase.get(requireContext()).settingsDao()

        // gets session id of this session
        // not sure if this crashes when called from climb fragment
        args = arguments?.getLong("sessionId")
        Log.d("CFF","args: ${arguments}")

        // call this when climbing session ends
        if (args == null) {
            insertValuesToUI()
        }

        // call this when this fragment is used to display session from statistics
        else {
            insertValuesToUIFromDatabase(args!!)
        }
    }

    private fun initValues(view: View) {
        // TextView
        resultTitle = view.findViewById(R.id.climb_finished_result_title)
        resultDetail = view.findViewById(R.id.climb_finished_result_detail)
        resultStartDifficulty = view.findViewById(R.id.climb_finished_difficulty_start)
        resultEndDifficulty = view.findViewById(R.id.climb_finished_difficulty_end)
        resultMode = view.findViewById(R.id.climb_finished_mode)
        resultTime = view.findViewById(R.id.climb_finished_time_value)
        resultLength = view.findViewById(R.id.climb_finished_length_value)
        resultCalories = view.findViewById(R.id.climb_finished_calories_value)
        resultSpeed = view.findViewById(R.id.climb_finished_speed_value)

        // MaterialButton
        toStatisticsBtn = view.findViewById(R.id.view_earlier_results_btn)

        toStatisticsBtn.setOnClickListener {
            navigateToStatistics()
        }
    }

    // Inserts values fetched from database to the corresponding UI elements
    // Currently uses dummy values. Eventually values will be fetched from database.
    private fun insertValuesToUI() {
        // fetch title from database
        val title = "Congratulations!"
        if (title == "Congratulations!") {
            val titleString =
                getString(R.string.fragment_climb_finished_result_title, "Congratulations!")
            resultTitle.text = titleString
        } else {
            // fetch date from database
            val date = "5.11.2021"
            val titleString = getString(R.string.fragment_climb_finished_result_title, date)
            resultTitle.text = titleString
        }

        // fetch lengths from database
        val actualLength = 18.3f
        val goalLength = 20f
        val titleDetailString =
            getString(R.string.fragment_climb_finished_result_detail, actualLength, goalLength)
        resultDetail.text = titleDetailString

        // fetch startDifficulty from database
        val startDifficulty = "Beginner"
        val startDifficultyString =
            getString(R.string.fragment_climb_finished_difficulty_start, startDifficulty)
        resultStartDifficulty.text = startDifficultyString

        // fetch endDifficulty from database
        val endDifficulty = "Athlete"
        val endDifficultyString =
            getString(R.string.fragment_climb_finished_difficulty_end, endDifficulty)
        resultEndDifficulty.text = endDifficultyString

        // fetch mode from database
        val mode = "To next difficulty"
        val modeString = getString(R.string.fragment_climb_finished_mode, mode)
        resultMode.text = modeString

        // fetch time from database
        val time = "00:14:22"
        val timeString = getString(R.string.fragment_climb_finished_time_value, time)
        resultTime.text = timeString

        // fetch length from database
        val length = 18.3f
        val lengthString = getString(R.string.fragment_climb_finished_length_value, length)
        resultLength.text = lengthString

        // fetch calories from database
        val calories = 140.6f
        val caloriesString = getString(R.string.fragment_climb_finished_calories_value, calories)
        resultCalories.text = caloriesString

        // fetch speed from database. probably average speed?
        val speed = 8.7f
        val speedString = getString(R.string.fragment_climb_finished_speed_value, speed)
        resultSpeed.text = speedString
    }

    private fun insertValuesToUIFromDatabase(args: Long) {
        // fetch all necessary items from database
        mainScope.launch {
            val session = getSession(args)
            val date = session.session.createdAt
            val actualLength = session.data.last().totalDistance.toFloat()
            val goalLength = 20f
            val startDifficulty = "Beginner"
            val endDifficulty = "Athlete"
            val mode = "To next difficulty"
            val duration = "00:05:17"
            val userWeight = settingsDao.getBodyWeightById(1).weight
            val calories = CalorieCounter().countCalories(actualLength, userWeight)

            val speedsArray = mutableListOf<Int>()
            for (item in session.data.indices) {
                speedsArray.add(session.data[item].speed)
            }

            val averageSpeed = speedsArray.average().toFloat()

            val titleString = getString(R.string.fragment_climb_finished_result_title, date)
            resultTitle.text = titleString

            val titleDetailString =
                getString(R.string.fragment_climb_finished_result_detail, actualLength, goalLength)
            resultDetail.text = titleDetailString

            val startDifficultyString =
                getString(R.string.fragment_climb_finished_difficulty_start, startDifficulty)
            resultStartDifficulty.text = startDifficultyString

            val endDifficultyString =
                getString(R.string.fragment_climb_finished_difficulty_end, endDifficulty)
            resultEndDifficulty.text = endDifficultyString

            val modeString = getString(R.string.fragment_climb_finished_mode, mode)
            resultMode.text = modeString

            val timeString = getString(R.string.fragment_climb_finished_time_value, duration)
            resultTime.text = timeString

            val lengthString =
                getString(R.string.fragment_climb_finished_length_value, actualLength)
            resultLength.text = lengthString

            val caloriesString =
                getString(R.string.fragment_climb_finished_calories_value, calories)
            resultCalories.text = caloriesString

            val speedString = getString(R.string.fragment_climb_finished_speed_value, averageSpeed)
            resultSpeed.text = speedString
        }
    }

    private suspend fun getSession(args: Long) = withContext(Dispatchers.IO) {
        sessionWithDataDao = AppDatabase.get(requireContext()).sessionDao()
        val getSession = sessionWithDataDao.getSessionWithData(args)
        Log.d("statistics_fragment", "${getSession}")
        return@withContext getSession
    }

    private fun navigateToStatistics() {
        val sfm = requireActivity().supportFragmentManager
        Log.d("MainActivity.kt", "BottomNavigation tracker clicked")
        val transaction = sfm.beginTransaction()
        transaction.replace<StatisticsFragment>(R.id.fragmentContainer)
        transaction.commit()
    }
}