package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.HorizontalPickerAdapter
import fi.climbstationsolutions.climbstation.ui.viewmodels.HorizontalNumberPickerViewModel

class AdjustFragment : Fragment() {
    private lateinit var horizontalNumberPickerViewModel: HorizontalNumberPickerViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var numberList: RecyclerView
    private lateinit var decrementNumber: AppCompatButton
    private lateinit var incrementNumber: AppCompatButton
    private var numbersListWidth: Int? = null
    private var myAdapter: HorizontalPickerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.isSmoothScrollbarEnabled = true

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adjust, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numbersListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.number_list_layout_width)

        numberList = view.findViewById(R.id.number_list)
        decrementNumber = view.findViewById(R.id.decrement_number)
        incrementNumber = view.findViewById(R.id.increment_number)

        horizontalNumberPickerViewModel =
            ViewModelProviders.of(this).get(HorizontalNumberPickerViewModel::class.java)

        myAdapter =
            HorizontalPickerAdapter(horizontalNumberPickerViewModel.numbers, context) { number ->
                horizontalNumberPickerViewModel.setSelectedNumber(number)
            }
        numberList.adapter = myAdapter
        numberList.layoutManager = layoutManager

        initializeSelectedNumber()

        Log.d("SF1", "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}")

        // This checks that scrolling has stopped, checks that the position we stopped on is valid,
        // and adjusts the selected number accordingly
        numberList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (numberList.width / numbersListWidth!! - 1) / 2
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in horizontalNumberPickerViewModel.numbers.indices &&
                        horizontalNumberPickerViewModel.numbers[position] != horizontalNumberPickerViewModel.selectedNumber.value
                    ) {
                        when (position) {
                            0 -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[0]
                                )
                                scrollToNumber(horizontalNumberPickerViewModel.numbers[0])
                            }
                            horizontalNumberPickerViewModel.numbers.size - 1 -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[position - 1]
                                )
                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            else -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[position]
                                )
                                Log.d("SF1", "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}")
                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position - 1], position)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initializeSelectedNumber() {
        if (horizontalNumberPickerViewModel.selectedNumber.value == null) {
            // currently manually set. Eventually will be set to the predetermined number
            val currentNumber = horizontalNumberPickerViewModel.numbers[10]
            horizontalNumberPickerViewModel.setSelectedNumber(currentNumber)
            scrollToNumber(currentNumber, currentNumber - 1)
        }
    }

    private fun scrollToNumber(number: Int, position: Int = 0) {
        var width = numberList.width
        if (width > 0) {
            val numberWidth = 150
            layoutManager.scrollToPositionWithOffset(
                horizontalNumberPickerViewModel.numbers.indexOf(
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
                            horizontalNumberPickerViewModel.numbers.indexOf(
                                number
                            ), width / 2 - numberWidth / 2
                        )
                    }
                    myAdapter?.setTextSize(40F, position + 1)
                }
            })
        }
    }
}