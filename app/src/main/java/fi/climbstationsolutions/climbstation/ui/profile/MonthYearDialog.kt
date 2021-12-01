package fi.climbstationsolutions.climbstation.ui.profile

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.MonthYearDialogBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.temporal.TemporalField
import java.util.*

class MonthYearDialog : DialogFragment() {
    companion object {
        const val TAG = "MonthYearDialog"
    }

    private lateinit var binding: MonthYearDialogBinding
    private var positiveListener: ((month: Int, year: Int) -> Unit) = { _, _ -> }
    private var negativeListener: (() -> Unit) = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = MonthYearDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.filter)
            .setView(binding.root)
            .setPositiveButton(R.string.filter) { _, _ ->
                val (month, year) = getPickerValues()
                positiveListener(month, year)
            }
            .setNegativeButton(R.string.clear) { _, _ ->
                negativeListener()
            }

        initMonthPicker()
        initYearPicker()

        return builder.create()
    }

    fun setPositiveListener(listener: (Int, Int) -> Unit) {
        positiveListener = listener
    }

    fun setNegativeListener(listener: () -> Unit) {
        negativeListener = listener
    }

    private fun initMonthPicker() {
        val months = Calendar.getInstance(TimeZone.getDefault())
            .getDisplayNames(Calendar.MONTH, Calendar.LONG_STANDALONE, Locale.ENGLISH) ?: return
        val currentMonth = LocalDate.now().monthValue - 1

        binding.pickerMonth.apply {
            minValue = 0
            maxValue = 11
            displayedValues = months.keys.toTypedArray()
            value = currentMonth
        }
    }

    private fun initYearPicker() {
        val today = Date()
        val calendar = GregorianCalendar()
        calendar.time = today
        val currentYear = calendar.get(Calendar.YEAR)
        binding.pickerYear.apply {
            minValue = 2021
            maxValue = currentYear
            value = currentYear
        }
    }

    private fun getPickerValues(): Pair<Int, Int> {
        val month = binding.pickerMonth.value + 1
        val year = binding.pickerYear.value
        return month to year
    }
}