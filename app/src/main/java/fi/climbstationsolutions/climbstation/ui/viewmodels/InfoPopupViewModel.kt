package fi.climbstationsolutions.climbstation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InfoPopupViewModel: ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

//    private val _instructions = MutableLiveData<String>()
//    val instructions: LiveData<String> = _instructions

    fun setTitle(title: String) {
        _title.value = title
    }

//    fun setInstructions(instructions: String) {
//        _instructions.value = instructions
//    }
}