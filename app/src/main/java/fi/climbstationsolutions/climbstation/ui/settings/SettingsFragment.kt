package fi.climbstationsolutions.climbstation.ui.settings

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.HorizontalPickerAdapter
import fi.climbstationsolutions.climbstation.ui.viewmodels.HorizontalNumberPickerViewModel

class SettingsFragment : Fragment() {
    private lateinit var horizontalNumberPickerViewModel: HorizontalNumberPickerViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var numberList: RecyclerView
    private lateinit var decrementNumber: AppCompatButton
    private lateinit var incrementNumber: AppCompatButton
    private var numbersListWidth: Int? = null
    private var myAdapter: HorizontalPickerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.isSmoothScrollbarEnabled = true

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}