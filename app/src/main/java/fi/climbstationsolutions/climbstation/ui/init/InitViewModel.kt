package fi.climbstationsolutions.climbstation.ui.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {
    private val mLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also { it.value = false }
    }
    val loading: LiveData<Boolean>
        get() = mLoading

    private fun setLoading(value: Boolean) {
        mLoading.value = value
    }

    var serial: String? = null
        private set

    fun testSerialNo(serialNo: String) {
        setLoading(true)

        viewModelScope.launch {
            serial = if(isLoginOk(serialNo)) serialNo else null
            setLoading(false)
        }
    }

    private suspend fun isLoginOk(serialNo: String): Boolean {
        var success = false
        try {
            val key = ClimbStationRepository.login(serialNo,
                BuildConfig.USERNAME,
                BuildConfig.PASSWORD
            )
            success = true
            ClimbStationRepository.logout(serialNo, key)
        } catch (e: Exception) {
            // Nothing to do here
        }
        return success
    }

}