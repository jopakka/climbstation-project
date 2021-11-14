package fi.climbstationsolutions.climbstation.ui.climb

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.climbstationsolutions.climbstation.network.profile.Profile

class ClimbViewModel: ViewModel() {
    private val mProfile: MutableLiveData<Profile> by lazy {
        MutableLiveData<Profile>()
    }
    val profile: LiveData<Profile>
        get() = mProfile

    fun postValue(profile: Profile) {
        mProfile.value = profile
    }
}