package fi.climbstationsolutions.climbstation.ui.climb.adjust

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.HorizontalNumberPickerAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentAdjustBinding
import fi.climbstationsolutions.climbstation.ui.viewmodels.AdjustViewModel

class
AdjustFragment : Fragment(R.layout.fragment_adjust), NumberPicker.OnValueChangeListener {
    private lateinit var binding: FragmentAdjustBinding

    private val viewModel: AdjustViewModel by viewModels()

    private lateinit var angleLayoutManager: LinearLayoutManager
    private lateinit var lengthLayoutManager: LinearLayoutManager

    private var angleListWidth: Int? = null
    private var lengthListWidth: Int? = null
    private var horizontalAnglePickerAdapter: HorizontalNumberPickerAdapter? = null
    private var horizontalLengthPickerAdapter: HorizontalNumberPickerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdjustBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.adjustFragmentTimepickerMinute.minValue = 0
        binding.adjustFragmentTimepickerSecond.minValue = 0
        binding.adjustFragmentTimepickerMinute.maxValue = 60
        binding.adjustFragmentTimepickerSecond.maxValue = 60

        angleLayoutManager = LinearLayoutManager(context)
        angleLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        angleLayoutManager.isSmoothScrollbarEnabled = true

        lengthLayoutManager = LinearLayoutManager(context)
        lengthLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        lengthLayoutManager.isSmoothScrollbarEnabled = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeVariables()
        initializeSelectedLength()
        initializeSelectedAngle()

        Log.d("SF1", "selected values: ${viewModel.getValues()}")

        binding.adjustFragmentAngleList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (binding.adjustFragmentAngleList.width / angleListWidth!! - 1) / 2
                    val position =
                        angleLayoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in viewModel.angleNumbers.indices &&
                        viewModel.angleNumbers[position] != viewModel.getValues()?.angle
                    ) {
                        when (position) {
                            0 -> {
                                viewModel.setAngle(
                                    viewModel.angleNumbers[0]
                                )
                                scrollToLength(viewModel.angleNumbers[0])
                            }
                            viewModel.angleNumbers.size - 1 -> {
                                viewModel.setAngle(
                                    viewModel.angleNumbers[position - 5]
                                )
                                Log.d(
                                    "SF1",
                                    "selected angle: ${viewModel.getValues()?.angle}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.angleNumbers.size - 2 -> {
                                viewModel.setAngle(
                                    viewModel.angleNumbers[position - 4]
                                )
                                Log.d(
                                    "SF1",
                                    "selected angle: ${viewModel.getValues()?.angle}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.angleNumbers.size - 3 -> {
                                viewModel.setAngle(
                                    viewModel.angleNumbers[position - 3]
                                )
                                Log.d(
                                    "SF1",
                                    "selected angle: ${viewModel.getValues()?.angle}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.angleNumbers.size - 4 -> {
                                viewModel.setAngle(
                                    viewModel.angleNumbers[position - 2]
                                )
                                Log.d(
                                    "SF1",
                                    "selected angle: ${viewModel.getValues()?.angle}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.angleNumbers.size - 5 -> {
                                viewModel.setAngle(
                                    viewModel.angleNumbers[position - 1]
                                )
                                Log.d(
                                    "SF1",
                                    "selected angle: ${viewModel.getValues()?.angle}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            else -> {
                                viewModel.setAngle(
                                    viewModel.angleNumbers[position]
                                )
                                Log.d(
                                    "SF1",
                                    "selected angle: ${viewModel.getValues()?.angle}"
                                )
                                scrollToAngle(
                                    viewModel.angleNumbers[position],
                                    position
                                )
                            }
                        }
                    }
                }
            }
        })

        // This checks that scrolling has stopped, checks that the position we stopped on is valid,
        // and adjusts the selected number accordingly
        binding.adjustFragmentLengthList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset =
                        (binding.adjustFragmentLengthList.width / lengthListWidth!! - 1) / 2
                    val position =
                        lengthLayoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in viewModel.lengthNumbers.indices &&
                        viewModel.lengthNumbers[position] != viewModel.getValues()?.length
                    ) {
                        when (position) {
                            0 -> {
                                viewModel.setLength(
                                    viewModel.lengthNumbers[0]
                                )
                                scrollToLength(viewModel.lengthNumbers[0])
                            }
                            viewModel.lengthNumbers.size - 1 -> {
                                viewModel.setLength(
                                    viewModel.lengthNumbers[position - 5]
                                )
                                Log.d(
                                    "SF1",
                                    "selected length: ${viewModel.getValues()?.length}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.lengthNumbers.size - 2 -> {
                                viewModel.setLength(
                                    viewModel.lengthNumbers[position - 4]
                                )
                                Log.d(
                                    "SF1",
                                    "selected length: ${viewModel.getValues()?.length}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.lengthNumbers.size - 3 -> {
                                viewModel.setLength(
                                    viewModel.lengthNumbers[position - 3]
                                )
                                Log.d(
                                    "SF1",
                                    "selected length: ${viewModel.getValues()?.length}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.lengthNumbers.size - 4 -> {
                                viewModel.setLength(
                                    viewModel.lengthNumbers[position - 2]
                                )
                                Log.d(
                                    "SF1",
                                    "selected length: ${viewModel.getValues()?.length}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            viewModel.lengthNumbers.size - 5 -> {
                                viewModel.setLength(
                                    viewModel.lengthNumbers[position - 1]
                                )
                                Log.d(
                                    "SF1",
                                    "selected length: ${viewModel.getValues()?.length}"
                                )
//                                scrollToNumber(horizontalNumberPickerViewModel.numbers[position + 1], position)
                            }
                            else -> {
                                viewModel.setLength(
                                    viewModel.lengthNumbers[position]
                                )
                                Log.d(
                                    "SF1",
                                    "selected length: ${viewModel.getValues()?.length}"
                                )
                                scrollToLength(
                                    viewModel.lengthNumbers[position],
                                    position
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initializeSelectedAngle() {
        if (viewModel.getValues()?.angle == null) {
            // currently manually set. Eventually will be set to the predetermined mode
            val currentAngle = viewModel.angleNumbers[1]
            viewModel.setAngle(currentAngle)
            scrollToAngle(currentAngle, viewModel.angleNumbers.indexOf(currentAngle))
        }
    }

    private fun initializeSelectedLength() {
        if (viewModel.getValues()?.length == null) {
            // currently manually set. Eventually will be set to the predetermined number
            val currentLength = viewModel.lengthNumbers[10]
            viewModel.setLength(currentLength)
            scrollToLength(currentLength, currentLength - 1)
        }
    }

    private fun scrollToAngle(angle: Int, position: Int = 0) {
        var width = binding.adjustFragmentAngleList.width
        if (width > 0) {
            angleLayoutManager.scrollToPosition(
                viewModel.angleNumbers.indexOf(
                    angle
                )
            )
            horizontalAnglePickerAdapter?.setTextSize(30F, position)

        } else {
            // waits for layout to finish loading, then scrolls
            val vto = binding.adjustFragmentAngleList.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.adjustFragmentAngleList.viewTreeObserver.removeOnGlobalLayoutListener(
                        this
                    )
                    width = binding.adjustFragmentAngleList.width
                    angleListWidth?.let { angleWidth ->
                        angleLayoutManager.scrollToPositionWithOffset(
                            viewModel.angleNumbers.indexOf(
                                angle
                            ), width / 2 - angleWidth / 2
                        )
                    }
                    horizontalAnglePickerAdapter?.setTextSize(30F, position + 1)
                }
            })
        }
    }

    private fun scrollToLength(length: Int, position: Int = 0) {
        var width = binding.adjustFragmentLengthList.width
        if (width > 0) {
            lengthLayoutManager.scrollToPosition(
                viewModel.lengthNumbers.indexOf(
                    length
                )
            )
            horizontalLengthPickerAdapter?.setTextSize(30F, position)

        } else {
            // waits for layout to finish loading, then scrolls
            val vto = binding.adjustFragmentLengthList.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.adjustFragmentLengthList.viewTreeObserver.removeOnGlobalLayoutListener(
                        this
                    )
                    width = binding.adjustFragmentLengthList.width
                    lengthListWidth?.let { lengthWidth ->
                        lengthLayoutManager.scrollToPositionWithOffset(
                            viewModel.lengthNumbers.indexOf(
                                length
                            ), width / 2 - lengthWidth / 2
                        )
                    }
                    horizontalLengthPickerAdapter?.setTextSize(30F, position + 1)
                }
            })
        }
    }

    private fun initializeVariables() {
        binding.adjustFragmentTimepickerMinute.minValue = 0
        binding.adjustFragmentTimepickerSecond.minValue = 0
        binding.adjustFragmentTimepickerMinute.maxValue = 60
        binding.adjustFragmentTimepickerSecond.maxValue = 60

        binding.adjustFragmentTimepickerMinute.setFormatter { i: Int ->
            String.format("%02d", i)
        }
        binding.adjustFragmentTimepickerSecond.setFormatter { i: Int ->
            String.format("%02d", i)
        }
        binding.adjustFragmentTimepickerMinute.setOnValueChangedListener(this)
        binding.adjustFragmentTimepickerSecond.setOnValueChangedListener(this)

        angleListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.string_list_layout_width)
        lengthListWidth =
            context?.resources?.getDimensionPixelSize(R.dimen.number_list_layout_width)

//        viewModel =
//            ViewModelProviders.of(this).get(AdjustViewModel::class.java)

        horizontalAnglePickerAdapter = HorizontalNumberPickerAdapter(
            viewModel.angleNumbers,
            context
        ) { angle -> viewModel.setAngle(angle) }

        horizontalLengthPickerAdapter = HorizontalNumberPickerAdapter(
            viewModel.lengthNumbers,
            context
        ) { length -> viewModel.setLength(length) }

        binding.adjustFragmentLengthList.adapter = horizontalLengthPickerAdapter
        binding.adjustFragmentLengthList.layoutManager = lengthLayoutManager
        binding.adjustFragmentAngleList.adapter = horizontalAnglePickerAdapter
        binding.adjustFragmentAngleList.layoutManager = angleLayoutManager
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        when (val tag = p0?.tag) {
            "minute" -> {
                viewModel.setMinute(p2)
            }
            "second" -> {
                viewModel.setSecond(p2)
            }
            else -> {
                Log.d("NumberPicker_onValueChange", "no NumberPicker with tag: $tag found")
            }
        }
    }
}