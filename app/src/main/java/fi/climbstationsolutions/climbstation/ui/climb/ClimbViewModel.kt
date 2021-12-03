package fi.climbstationsolutions.climbstation.ui.climb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps

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
        mProfileWithSteps.postValue(profile)
    }

    fun setLoading(value: Boolean) {
        mLoading.value = value
    }
}
