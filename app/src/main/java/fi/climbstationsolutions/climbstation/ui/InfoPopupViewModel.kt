package fi.climbstationsolutions.climbstation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InfoPopupViewModel : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    fun setTitle(title: String) {
        _title.value = title
    }
}