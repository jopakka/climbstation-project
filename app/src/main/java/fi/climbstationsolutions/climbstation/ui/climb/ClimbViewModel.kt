package fi.climbstationsolutions.climbstation.ui.climb

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClimbViewModel: ViewModel() {
    private val mProfile: MutableLiveData<DifficultyProfile> by lazy {
        MutableLiveData<DifficultyProfile>().also {
            it.value = DifficultyProfile("Testi", 1, 2, 3)
        }
    }
    val profile: LiveData<DifficultyProfile>
        get() = mProfile

    fun postValue(prof: DifficultyProfile) {
        mProfile.value = prof
    }
}