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
import fi.climbstationsolutions.climbstation.ui.viewmodels.HorizontalPickerViewModel

class SettingsFragment : Fragment() {
    private lateinit var horizontalPickerViewModel: HorizontalPickerViewModel
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numbersListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.number_list_layout_width)

        numberList = view.findViewById(R.id.number_list)
        decrementNumber = view.findViewById(R.id.decrement_number)
        incrementNumber = view.findViewById(R.id.increment_number)

        horizontalPickerViewModel =
            ViewModelProviders.of(this).get(HorizontalPickerViewModel::class.java)

        myAdapter =
            HorizontalPickerAdapter(horizontalPickerViewModel.numbers, context) { number ->
                horizontalPickerViewModel.setSelectedNumber(number)
//                scrollToNumber(number)
            }
        numberList.adapter = myAdapter
        numberList.layoutManager = layoutManager

        initializeSelectedNumber()

        Log.d("HPV", "selectedNumber1: ${horizontalPickerViewModel.selectedNumber.value}")

//        decrementNumber.setOnClickListener {
//            horizontalPickerViewModel.selectedNumber.value?.let { number ->
//                val previousNumber = number - 1
//                if (horizontalPickerViewModel.numbers.indexOf(number) >= 0) {
//                    horizontalPickerViewModel.setSelectedNumber(previousNumber)
//                    Log.d(
//                        "HPV",
//                        "selectedNumber2: ${horizontalPickerViewModel.selectedNumber.value}"
//                    )
//                    val offset = (numberList.width / numbersListWidth!! - 1) / 2
//                    val position = layoutManager.findFirstCompletelyVisibleItemPosition() + offset
//                    scrollToNumber(previousNumber, position)
//                }
//            }
//        }
//
//        incrementNumber.setOnClickListener {
//            horizontalPickerViewModel.selectedNumber.value?.let { number ->
//                val previousNumber = number + 1
//                if (horizontalPickerViewModel.numbers.indexOf(number) >= 0) {
//                    horizontalPickerViewModel.setSelectedNumber(previousNumber)
//                    Log.d(
//                        "HPV",
//                        "selectedNumber3: ${horizontalPickerViewModel.selectedNumber.value}"
//                    )
//                    val offset = (numberList.width / numbersListWidth!! - 1) / 2
//                    val position = layoutManager.findFirstCompletelyVisibleItemPosition() + offset
//                    scrollToNumber(previousNumber, position)
//                }
//            }
//        }

        // This checks that scrolling has stopped, checks that the position we stopped on is valid,
        // and adjusts the selected number accordingly
        numberList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (numberList.width / numbersListWidth!! - 1) / 2
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in horizontalPickerViewModel.numbers.indices &&
                        horizontalPickerViewModel.numbers[position] != horizontalPickerViewModel.selectedNumber.value
                    ) {
                        when (position) {
                            0 -> {
                                Log.d("HPV", "position: $position")
                                horizontalPickerViewModel.setSelectedNumber(
                                    horizontalPickerViewModel.numbers[0]
                                )
                                Log.d(
                                    "HPV",
                                    "selectedNumber: ${horizontalPickerViewModel.selectedNumber.value}"
                                )
                                scrollToNumber(horizontalPickerViewModel.numbers[0])
                            }
                            horizontalPickerViewModel.numbers.size - 1 -> {
                                horizontalPickerViewModel.setSelectedNumber(
                                    horizontalPickerViewModel.numbers[position - 1]
                                )
                                Log.d(
                                    "HPV",
                                    "selectedNumber: ${horizontalPickerViewModel.selectedNumber.value}"
                                )
                                scrollToNumber(horizontalPickerViewModel.numbers[position - 1])
                            }
                            else -> {
                                Log.d("HPV", "position: $position")
                                horizontalPickerViewModel.setSelectedNumber(
                                    horizontalPickerViewModel.numbers[position]
                                )
                                Log.d(
                                    "HPV",
                                    "selectedNumber: ${horizontalPickerViewModel.selectedNumber.value}"
                                )
                                scrollToNumber(horizontalPickerViewModel.numbers[position - 1], position)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initializeSelectedNumber() {
        if (horizontalPickerViewModel.selectedNumber.value == null) {

            val offset = (numberList.width / numbersListWidth!! - 1) / 2
            val position = layoutManager.findFirstCompletelyVisibleItemPosition() + offset
            // currently manually set. Eventually will be set to the predetermined number
            val currentNumber = horizontalPickerViewModel.numbers[10]
            horizontalPickerViewModel.setSelectedNumber(currentNumber)
            scrollToNumber(currentNumber, currentNumber - 1)
        }
    }

    private fun scrollToNumber(number: Int, position: Int = 0) {
        var width = numberList.width
        if (width > 0) {
            val numberWidth = 150
            layoutManager.scrollToPositionWithOffset(
                horizontalPickerViewModel.numbers.indexOf(
                    number
                ), width / 2 - numberWidth / 2
            )
            myAdapter?.setTextSize(40F, position)

        } else {
            // waits for layout to finish loading, then scrolls
            val vto = numberList.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    numberList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = numberList.width
                    numbersListWidth?.let { numberWidth ->
                        layoutManager.scrollToPositionWithOffset(
                            horizontalPickerViewModel.numbers.indexOf(
                                number
                            ), width / 2 - numberWidth / 2
                        )
                    }
                }
            })
        }
    }
}