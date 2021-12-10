package fi.climbstationsolutions.climbstation.ui.share

import android.app.Application
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfile
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.ClimbStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.get(application)
    private val profileDao = db.profileDao()

    private val mProfile: MutableLiveData<ClimbProfileWithSteps> by lazy {
        MutableLiveData<ClimbProfileWithSteps>()
    }
    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfile

    private val mLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val loading: LiveData<Boolean>
        get() = mLoading

    fun setProfileWithSteps(profileWithSteps: ClimbProfileWithSteps) {
        mProfile.value = profileWithSteps
    }

    private fun setLoading(value: Boolean) {
        mLoading.postValue(value)
    }

    fun saveProfile(name: String) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val profWithSteps = profileWithSteps.value ?: return@launch
            val profile = ClimbProfile(0, name, createdAt = profWithSteps.profile.createdAt)
            val profileId = profileDao.insertProfile(profile)
            profWithSteps.steps.forEach {
                val step = ClimbStep(0, profileId, it.distance, it.angle)
                profileDao.insertStep(step)
            }
            setLoading(false)
        }
    }
}