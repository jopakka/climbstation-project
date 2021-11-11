package fi.climbstationsolutions.climbstation.ui.climb.adjust

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.HorizontalNumberPickerAdapter
import fi.climbstationsolutions.climbstation.adapters.HorizontalStringPickerAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentAdjustBinding
import fi.climbstationsolutions.climbstation.ui.viewmodels.AdjustViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.HorizontalNumberPickerViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.HorizontalStringPickerViewModel

class
AdjustFragment : Fragment(R.layout.fragment_adjust) {
    private lateinit var binding: FragmentAdjustBinding

    private val viewModel: AdjustViewModel by viewModels()

    private lateinit var numberLayoutManager: LinearLayoutManager
    private lateinit var stringLayoutManager: LinearLayoutManager

    private var stringsListWidth: Int? = null
    private var numbersListWidth: Int? = null
    private var horizontalNumberPickerAdapter: HorizontalNumberPickerAdapter? = null
    private var horizontalStringPickerAdapter: HorizontalStringPickerAdapter? = null
    private var speed = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdjustBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        numberLayoutManager = LinearLayoutManager(context)
        numberLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        numberLayoutManager.isSmoothScrollbarEnabled = true

        stringLayoutManager = LinearLayoutManager(context)
        stringLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        stringLayoutManager.isSmoothScrollbarEnabled = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeVariables()
        initializeSelectedNumber()
        initializeSelectedString()

        Log.d("SF1", "selected number: ${viewModel.selectedLength.value}")

        // This checks that scrolling has stopped, checks that the position we stopped on is valid,
        // and adjusts the selected number accordingly
        binding.numberList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (binding.numberList.width / numbersListWidth!! - 1) / 2
                    val position = numberLayoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in viewModel.numbers.indices &&
                        viewModel.numbers[position] != viewModel.selectedLength.value
                    ) {
                        when (position) {
                            0 -> {
                                viewModel.setSelectedLength(
                                    viewModel.numbers[0]
                                )
                                scrollToNumber(viewModel.numbers[0])
                            }
                            viewModel.numbers.size - 1 -> {
                                viewModel.setSelectedLength(
                                    viewModel.numbers[position - 5]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${viewModel.selectedLength.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.numbers.size - 2 -> {
                                viewModel.setSelectedLength(
                                    viewModel.numbers[position - 4]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${viewModel.selectedLength.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.numbers.size - 3 -> {
                                viewModel.setSelectedLength(
                                    viewModel.numbers[position - 3]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${viewModel.selectedLength.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.numbers.size - 4 -> {
                                viewModel.setSelectedLength(
                                    viewModel.numbers[position - 2]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${viewModel.selectedLength.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.numbers.size - 5 -> {
                                viewModel.setSelectedLength(
                                    viewModel.numbers[position - 1]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${viewModel.selectedLength.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            else -> {
                                viewModel.setSelectedLength(
                                    viewModel.numbers[position]
                                )
                                Log.d(
                                    "SF1",
                                    "selected number: ${viewModel.selectedLength.value}"
                                )
                                scrollToNumber(
                                    viewModel.numbers[position],
                                    position
                                )
                            }
                        }
                    }
                }
            }
        })

        binding.stringList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (binding.stringList.width / stringsListWidth!! - 1) / 2
                    val position = stringLayoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in viewModel.strings.indices &&
                        viewModel.strings[position] != viewModel.selectedMode.value
                    ) {
                        when (position) {
                            0 -> {
                                viewModel.setSelectedMode(
                                    viewModel.strings[0]
                                )
                                scrollToString(viewModel.strings[0])
                            }
                            viewModel.strings.size - 1 -> {
                                viewModel.setSelectedMode(
                                    viewModel.strings[position - 1]
                                )
                                Log.d(
                                    "SF2",
                                    "selected string: ${viewModel.selectedMode.value}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            else -> {
                                viewModel.setSelectedMode(
                                    viewModel.strings[position]
                                )
                                Log.d(
                                    "SF2",
                                    "selected string: ${viewModel.selectedMode.value}"
                                )
                                scrollToString(
                                    viewModel.strings[position],
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
        if (viewModel.selectedLength.value == null) {
            // currently manually set. Eventually will be set to the predetermined number
            val currentNumber = viewModel.numbers[10]
            viewModel.setSelectedLength(currentNumber)
            scrollToNumber(currentNumber, currentNumber - 1)
        }
    }

    private fun initializeSelectedString() {
        if (viewModel.selectedMode.value == null) {
            // currently manually set. Eventually will be set to the predetermined mode
            val currentMode = viewModel.strings[1]
            viewModel.setSelectedMode(currentMode)
            scrollToString(currentMode, viewModel.strings.indexOf(currentMode))
        }
    }

    private fun scrollToNumber(number: Int, position: Int = 0) {
        var width = binding.numberList.width
        if (width > 0) {
            numberLayoutManager.scrollToPosition(
                viewModel.numbers.indexOf(
                    number
                )
            )
            horizontalNumberPickerAdapter?.setTextSize(30F, position)

        } else {
            // waits for layout to finish loading, then scrolls
            val vto = binding.numberList.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.numberList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = binding.numberList.width
                    numbersListWidth?.let { numberWidth ->
                        numberLayoutManager.scrollToPositionWithOffset(
                            viewModel.numbers.indexOf(
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
        var width = binding.stringList.width
        if (width > 0) {
            stringLayoutManager.scrollToPosition(
                viewModel.strings.indexOf(
                    string
                )
            )
            horizontalStringPickerAdapter?.setTextSize(20F, position)

        } else {
            // waits for layout to finish loading, then scrolls
            val vto = binding.stringList.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.stringList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = binding.stringList.width
                    stringsListWidth?.let { stringWidth ->
                        stringLayoutManager.scrollToPositionWithOffset(
                            viewModel.strings.indexOf(
                                string
                            ), width / 2 - stringWidth / 2
                        )
                    }
                    horizontalStringPickerAdapter?.setTextSize(20F, position + 1)
                }
            })
        }
    }

    private fun initializeVariables() {
        numbersListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.number_list_layout_width)
        stringsListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.string_list_layout_width)

//        viewModel =
//            ViewModelProviders.of(this).get(AdjustViewModel::class.java)


        horizontalNumberPickerAdapter =
            HorizontalNumberPickerAdapter(
                viewModel.numbers,
                context
            ) { number ->
                viewModel.setSelectedLength(number)
            }
        horizontalStringPickerAdapter = HorizontalStringPickerAdapter(
            viewModel.strings,
            context
        ) { string -> viewModel.setSelectedMode(string) }

        binding.numberList.adapter = horizontalNumberPickerAdapter
        binding.numberList.layoutManager = numberLayoutManager
        binding.stringList.adapter = horizontalStringPickerAdapter
        binding.stringList.layoutManager = stringLayoutManager

        binding.speedSeekbar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                speed = p1
                viewModel.setSelectedSpeed(speed)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }
}