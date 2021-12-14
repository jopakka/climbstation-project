package fi.climbstationsolutions.climbstation.ui.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.climbstationsolutions.climbstation.BuildConfig
import fi.climbstationsolutions.climbstation.network.ClimbStationRepository
import kotlinx.coroutines.async

class InitViewModel : ViewModel() {
    private val mLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also { it.value = false }
    }
    val loading: LiveData<Boolean>
        get() = mLoading

    private fun setLoading(value: Boolean) {
        mLoading.value = value
    }

    private val mSerial: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also { it.value = null }
    }
    val serial: LiveData<String>
        get() = mSerial

    private fun setSerial(txt: String?) {
        mSerial.value = txt
    }

    suspend fun testSerialNo(serialNo: String): String? {
        setLoading(true)

        val response = viewModelScope.async {
            val (result, message) = isLoginOk(serialNo)
            if (result) setSerial(serialNo)
            setLoading(false)
            message
        }
        return response.await()
    }

    private suspend fun isLoginOk(serialNo: String): Pair<Boolean, String?> {
        var success = false
        var message: String? = null

        try {
            val key = ClimbStationRepository.login(
                serialNo,
                BuildConfig.USERNAME,
                BuildConfig.PASSWORD
            )
            success = true
            ClimbStationRepository.logout(serialNo, key)
        } catch (e: Exception) {
            message = e.localizedMessage
        }
        return success to message
    }

}