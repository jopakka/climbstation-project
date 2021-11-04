package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.HorizontalNumberPickerAdapter
import fi.climbstationsolutions.climbstation.adapters.HorizontalStringPickerAdapter
import fi.climbstationsolutions.climbstation.ui.viewmodels.HorizontalNumberPickerViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.HorizontalStringPickerViewModel

class AdjustFragment : Fragment() {
    private lateinit var horizontalNumberPickerViewModel: HorizontalNumberPickerViewModel
    private lateinit var horizontalStringPickerViewModel: HorizontalStringPickerViewModel
    private lateinit var numberLayoutManager: LinearLayoutManager
    private lateinit var stringLayoutManager: LinearLayoutManager
    private lateinit var numberList: RecyclerView
    private lateinit var stringList: RecyclerView
    private lateinit var seekBar: SeekBar
    private lateinit var speedText: TextView

    private var stringsListWidth: Int? = null
    private var numbersListWidth: Int? = null
    private var horizontalNumberPickerAdapter: HorizontalNumberPickerAdapter? = null
    private var horizontalStringPickerAdapter: HorizontalStringPickerAdapter? = null
    private var speed = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        numberLayoutManager = LinearLayoutManager(context)
        numberLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        numberLayoutManager.isSmoothScrollbarEnabled = true

        stringLayoutManager = LinearLayoutManager(context)
        stringLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        stringLayoutManager.isSmoothScrollbarEnabled = true

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adjust, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeVariables(view)
        initializeSelectedNumber()
        initializeSelectedString()

        Log.d("SF1", "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}")

        // This checks that scrolling has stopped, checks that the position we stopped on is valid,
        // and adjusts the selected number accordingly
        numberList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (numberList.width / numbersListWidth!! - 1) / 2
                    val position = numberLayoutManager.findFirstCompletelyVisibleItemPosition() + offset
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
                                    horizontalNumberPickerViewModel.numbers[position - 5]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            horizontalNumberPickerViewModel.numbers.size - 2 -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[position - 4]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            horizontalNumberPickerViewModel.numbers.size - 3 -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[position - 3]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            horizontalNumberPickerViewModel.numbers.size - 4 -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[position - 2]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            horizontalNumberPickerViewModel.numbers.size - 5 -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[position - 1]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            else -> {
                                horizontalNumberPickerViewModel.setSelectedNumber(
                                    horizontalNumberPickerViewModel.numbers[position]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${horizontalNumberPickerViewModel.selectedNumber.value}"
                                )
                                scrollToNumber(
                                    horizontalNumberPickerViewModel.numbers[position],
                                    position
                                )
                            }
                        }
                    }
                }
            }
        })

        stringList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (stringList.width / stringsListWidth!! - 1) / 2
                    val position = stringLayoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in horizontalStringPickerViewModel.strings.indices &&
                        horizontalStringPickerViewModel.strings[position] != horizontalStringPickerViewModel.selectedString.value
                    ) {
                        when (position) {
                            0 -> {
                                horizontalStringPickerViewModel.setSelectedString(
                                    horizontalStringPickerViewModel.strings[0]
                                )
                                scrollToString(horizontalStringPickerViewModel.strings[0])
                            }
                            horizontalStringPickerViewModel.strings.size - 1 -> {
                                horizontalStringPickerViewModel.setSelectedString(
                                    horizontalStringPickerViewModel.strings[position - 1]
                                )
                                Log.d(
                                    "SF2",
                                    "selected string: ${horizontalStringPickerViewModel.selectedString.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            else -> {
                                horizontalStringPickerViewModel.setSelectedString(
                                    horizontalStringPickerViewModel.strings[position]
                                )
                                Log.d(
                                    "SF2",
                                    "selected string: ${horizontalStringPickerViewModel.selectedString.value}"
                                )
                                scrollToString(
                                    horizontalStringPickerViewModel.strings[position],
                                    position
                                )
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

    private fun initializeSelectedString() {
        if (horizontalStringPickerViewModel.selectedString.value == null) {
            // currently manually set. Eventually will be set to the predetermined mode
            val currentString = horizontalStringPickerViewModel.strings[1]
            horizontalStringPickerViewModel.setSelectedString(currentString)
            scrollToString(currentString, horizontalStringPickerViewModel.strings.indexOf(currentString))
        }
    }

    private fun scrollToNumber(number: Int, position: Int = 0) {
        var width = numberList.width
        if (width > 0) {
            numberLayoutManager.scrollToPosition(
                horizontalNumberPickerViewModel.numbers.indexOf(
                    number
                )
            )
            horizontalNumberPickerAdapter?.setTextSize(30F, position)

        } else {
            // waits for layout to finish loading, then scrolls
            val vto = numberList.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    numberList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = numberList.width
                    numbersListWidth?.let { numberWidth ->
                        numberLayoutManager.scrollToPositionWithOffset(
                            horizontalNumberPickerViewModel.numbers.indexOf(
                                number
                            ), width / 2 - numberWidth / 2
                        )
                    }
                    horizontalNumberPickerAdapter?.setTextSize(30F, position + 1)
                }
            })
        }
    }

    private fun scrollToString(string: String, position: Int = 0) {
        var width = stringList.width
        if (width > 0) {
            stringLayoutManager.scrollToPosition(
                horizontalStringPickerViewModel.strings.indexOf(
                    string
                )
            )
            horizontalStringPickerAdapter?.setTextSize(20F, position)

        } else {
            // waits for layout to finish loading, then scrolls
            val vto = stringList.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    stringList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = stringList.width
                    stringsListWidth?.let { stringWidth ->
                        stringLayoutManager.scrollToPositionWithOffset(
                            horizontalStringPickerViewModel.strings.indexOf(
                                string
                            ), width / 2 - stringWidth / 2
                        )
                    }
                    horizontalStringPickerAdapter?.setTextSize(20F, position + 1)
                }
            })
        }
    }

    private fun initializeVariables(view: View) {
        seekBar = view.findViewById(R.id.speed_seekbar)
        speedText = view.findViewById(R.id.speed_value)
        speedText.text = (getString(R.string.fragment_adjust_speed, speed) + " m / min")

        numberList = view.findViewById(R.id.number_list)
        stringList = view.findViewById(R.id.string_list)

        numbersListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.number_list_layout_width)
        stringsListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.string_list_layout_width)

        horizontalNumberPickerViewModel =
            ViewModelProviders.of(this).get(HorizontalNumberPickerViewModel::class.java)
        horizontalStringPickerViewModel =
            ViewModelProviders.of(this).get(HorizontalStringPickerViewModel::class.java)

        horizontalNumberPickerAdapter =
            HorizontalNumberPickerAdapter(
                horizontalNumberPickerViewModel.numbers,
                context
            ) { number ->
                horizontalNumberPickerViewModel.setSelectedNumber(number)
            }
        horizontalStringPickerAdapter = HorizontalStringPickerAdapter(
            horizontalStringPickerViewModel.strings,
            context
        ) { string -> horizontalStringPickerViewModel.setSelectedString(string) }

        numberList.adapter = horizontalNumberPickerAdapter
        numberList.layoutManager = numberLayoutManager
        stringList.adapter = horizontalStringPickerAdapter
        stringList.layoutManager = stringLayoutManager

        seekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                speed = p1
                speedText.text = (getString(R.string.fragment_adjust_speed, speed) + " m / min")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })


    }
}