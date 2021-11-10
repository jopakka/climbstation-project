package fi.climbstationsolutions.climbstation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdjustViewModel: ViewModel() {
    private val _selectedLength = MutableLiveData<Int>()
    val selectedLength: LiveData<Int> = _selectedLength

    private val _selectedSpeed = MutableLiveData<Int>()
    val selectedSpeed: LiveData<Int> = _selectedSpeed

    private val _selectedMode = MutableLiveData<String>()
    val selectedMode: LiveData<String> = _selectedMode

    // amount of numbers in length picker
    private val myAmount = 100
    val numbers: List<Int>
        get() {
            val numbersMutableList = mutableListOf(0)
            for(i in 1..myAmount + 5) {
                numbersMutableList.add(i)
            }
            return numbersMutableList
        }

    // modes
    val strings: List<String>
        get() {
            return mutableListOf("", "TO NEXT DIFFICULTY", "SLOW DOWN", "testi1", "testi2", "testi3", "testi4", "")
        }

    fun setSelectedLength(length: Int) {
        _selectedLength.value = length
    }

    fun setSelectedSpeed(speed: Int) {
        _selectedSpeed.value = speed
    }

    fun setSelectedMode(mode: String) {
        _selectedMode.value = mode
    }
}