package fi.climbstationsolutions.climbstation.ui.climb

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.network.profile.Profile
import fi.climbstationsolutions.climbstation.ui.climb.climbOn.ClimbOnViewModel

class ClimbViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.get(getApplication())
    private val profileDao = db.profileDao()

    val allProfiles = profileDao.getAllProfiles()

    private val mProfileWithSteps: MutableLiveData<ClimbProfileWithSteps> by lazy {
        MutableLiveData<ClimbProfileWithSteps>()
    }
    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfileWithSteps

    private val mLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also { it.value = false }
    }
    val loading: LiveData<Boolean>
        get() = mLoading

    fun setProfile(profile: ClimbProfileWithSteps) {
        mProfileWithSteps.value = profile
    }

    fun setLoading(value: Boolean) {
        mLoading.value = value
    }
}
