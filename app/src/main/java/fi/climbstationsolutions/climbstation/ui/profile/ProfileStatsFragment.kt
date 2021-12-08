package fi.climbstationsolutions.climbstation.ui.profile

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.gson.internal.bind.ArrayTypeAdapter
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentProfileStatsBinding

class ProfileStatsFragment : Fragment() {
    private lateinit var binding: FragmentProfileStatsBinding
    private val viewModel: ProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileStatsBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerList: MutableList<String> = ArrayList()
        spinnerList.add("Distance")
        spinnerList.add("Avg angle")
        spinnerList.add("Time")
        spinnerList.add("Calories")

        val customSpinnerAdapter = ArrayAdapter(
            requireActivity().applicationContext,
            R.layout.custom_spinner_item,
            spinnerList
        )

        binding.profileGraphSpinner.adapter = customSpinnerAdapter

        binding.profileGraphSelectDay.setOnClickListener(clickListener)
        binding.profileGraphSelectWeek.setOnClickListener(clickListener)
        binding.profileGraphSelectMonth.setOnClickListener(clickListener)
        binding.profileGraphSelectYear.setOnClickListener(clickListener)

        selectVariable(spinnerList)

        selectGraphButton(binding.profileGraphSelectDay)
        viewModel.setTime("Today")
        viewModel.setTime2("08 Dec")

        //Settings the data for the graph. Currently manual
        val dataPoints = arrayOf(
            DataPoint(0.0, 22.0),
            DataPoint(1.0, 60.0),
            DataPoint(2.0, 30.0),
            DataPoint(3.0, 40.0),
            DataPoint(4.0, 90.0),
            DataPoint(5.0, 60.0),
            DataPoint(6.0, 70.0),
            DataPoint(7.0, 120.0),
            DataPoint(8.0, 70.0),
            DataPoint(9.0, 85.0),
            DataPoint(10.0, 100.0),
            DataPoint(11.0, 0.0),
            DataPoint(12.0, 0.0),
            DataPoint(13.0, 0.0),
            DataPoint(14.0, 0.0),
            DataPoint(15.0, 0.0),
            DataPoint(16.0, 2.0),
            DataPoint(17.0, 3.0),
            DataPoint(18.0, 5.0),
            DataPoint(19.0, 30.0),
            DataPoint(20.0, 40.0),
            DataPoint(21.0, 55.0),
            DataPoint(22.0, 22.0),
            DataPoint(23.0, 70.0),
        )
        val series = BarGraphSeries(dataPoints)
        series.color = ContextCompat.getColor(requireContext(), R.color.climbstation_red)
        series.spacing = 5
        series.isDrawValuesOnTop = true;
        series.valuesOnTopColor = Color.WHITE;
        binding.graphView.addSeries(series)
        series.valuesOnTopSize = 25F

        // Settings graph UI
        binding.graphView.gridLabelRenderer.numHorizontalLabels = 7
        binding.graphView.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        binding.graphView.viewport.setDrawBorder(true)
        binding.graphView.gridLabelRenderer.gridColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        binding.graphView.gridLabelRenderer.verticalLabelsColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        binding.graphView.gridLabelRenderer.horizontalLabelsColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        binding.graphView.gridLabelRenderer.setHumanRounding(true)
        binding.graphView.viewport.setMaxX(25.0)
        binding.graphView.viewport.isXAxisBoundsManual = true
//        binding.graphView.gridLabelRenderer.setHorizontalLabelsAngle(45)
    }

    private val clickListener = View.OnClickListener {
        resetButtonSelection()
        when (it) {
            binding.profileGraphSelectDay -> {
                selectGraphButton(it as MaterialButton)
                viewModel.setTime("Today")
                viewModel.setTime2("08 Dec")
            }
            binding.profileGraphSelectWeek -> {
                selectGraphButton(it as MaterialButton)
                viewModel.setTime("This week")
                viewModel.setTime2("06 - 12 Dec")
            }
            binding.profileGraphSelectMonth -> {
                selectGraphButton(it as MaterialButton)
                viewModel.setTime("This month")
                viewModel.setTime2("December")
            }
            binding.profileGraphSelectYear -> {
                selectGraphButton(it as MaterialButton)
                viewModel.setTime("This year")
                viewModel.setTime2("2021")
            }
        }
    }

    private fun selectVariable(
        spinnerList: MutableList<String>
    ) {
        binding.profileGraphSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    Log.d("spinner", "clicked")
                    viewModel.setVariable(spinnerList[p2])
                    Log.d("spinner", "spinnerlist: ${spinnerList[p2]}")
                    Log.d("spinner", "viewmodel: ${viewModel.graphVariable.value}")
                }

                // Unused, here to prevent member implementation error
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun resetButtonSelection() {
        binding.profileGraphSelectDay.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.graph_button_semitransparent
            )
        )
        binding.profileGraphSelectDay.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.profileGraphSelectWeek.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.graph_button_semitransparent
            )
        )
        binding.profileGraphSelectWeek.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.profileGraphSelectMonth.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.graph_button_semitransparent
            )
        )
        binding.profileGraphSelectMonth.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.profileGraphSelectYear.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.graph_button_semitransparent
            )
        )
        binding.profileGraphSelectYear.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
    }

    private fun selectGraphButton(view: MaterialButton) {
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.climbstation_red))
        view.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }
}