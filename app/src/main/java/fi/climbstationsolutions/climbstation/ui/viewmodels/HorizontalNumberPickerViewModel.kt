package fi.climbstationsolutions.climbstation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HorizontalNumberPickerViewModel: ViewModel() {
    private var myAmount: Int = 100
    private val _selectedNumber = MutableLiveData<Int>()
    val selectedNumber: LiveData<Int> = _selectedNumber

    fun setNumberAmount(value: Int) {
        myAmount = value
    }

    fun getNumberAmount(): Int {
        return myAmount
    }

    fun setSelectedNumber(num: Int) {
        _selectedNumber.value = num
    }

    val numbers: List<Int>
        get() {
            val numbersMutableList = mutableListOf(0)
                for(i in 1..myAmount + 1) {
                numbersMutableList.add(i)
            }
            return numbersMutableList
        }
}