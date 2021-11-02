package fi.climbstationsolutions.climbstation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HorizontalPickerViewModel: ViewModel() {
    private val _selectedNumber = MutableLiveData<Int>()
    val selectedNumber: LiveData<Int> = _selectedNumber

    fun setSelectedNumber(num: Int) {
        _selectedNumber.value = num
    }

    val numbers: List<Int>
        get() {
            return mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
        }
}