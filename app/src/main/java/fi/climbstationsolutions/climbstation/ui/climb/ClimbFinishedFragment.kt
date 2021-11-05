package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.replace
import com.google.android.material.button.MaterialButton
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsFragment

class ClimbFinishedFragment : Fragment() {
    private lateinit var toStatisticsBtn: MaterialButton

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

        toStatisticsBtn = view.findViewById(R.id.view_earlier_results_btn)

        toStatisticsBtn.setOnClickListener {
            navigateToStatistics()
        }
    }

    private fun navigateToStatistics() {
        val sfm = requireActivity().supportFragmentManager
        Log.d("MainActivity.kt","BottomNavigation tracker clicked")
        val transaction = sfm.beginTransaction()
        transaction.replace<StatisticsFragment>(R.id.fragmentContainer)
        transaction.commit()
    }
}