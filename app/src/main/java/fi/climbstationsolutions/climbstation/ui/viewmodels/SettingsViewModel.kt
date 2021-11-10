package fi.climbstationsolutions.climbstation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    private val _userWeight = MutableLiveData<Float>()
    val userWeight: LiveData<Float> = _userWeight

    fun setUserWeight(weight: Float) {
        _userWeight.value = weight
    }
}