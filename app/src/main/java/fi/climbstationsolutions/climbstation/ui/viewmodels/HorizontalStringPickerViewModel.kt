package fi.climbstationsolutions.climbstation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HorizontalStringPickerViewModel: ViewModel()  {
    private val _selectedString = MutableLiveData<String>()
    val selectedString: LiveData<String> = _selectedString

    fun setSelectedString(string: String) {
        _selectedString.value = string
    }

    val strings: List<String>
        get() {
            return mutableListOf("", "TO NEXT DIFFICULTY", "SLOW DOWN", "testi1", "testi2", "testi3", "testi4", "")
        }
}