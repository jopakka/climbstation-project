package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.replace
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.ui.settings.SettingsFragment

class ClimbFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_climb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toAdjustBtn = view.findViewById<Button>(R.id.btnAdjust)
        toAdjustBtn.setOnClickListener {
            navigateToAdjust()
        }
    }

    private fun navigateToAdjust() {
        val sfm = requireActivity().supportFragmentManager
        Log.d("MainActivity.kt","BottomNavigation tracker clicked")
        val transaction = sfm.beginTransaction()
        transaction.replace<AdjustFragment>(R.id.fragmentContainer)
        transaction.commit()
    }
}