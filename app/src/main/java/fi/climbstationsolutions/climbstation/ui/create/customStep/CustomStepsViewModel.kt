package fi.climbstationsolutions.climbstation.ui.create.customStep

import android.app.Application
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.ClimbStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomStepsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.get(getApplication())
    private val profileDao = db.profileDao()

    private val mCustomProfileWithSteps: MediatorLiveData<ClimbProfileWithSteps> by lazy {
        MediatorLiveData<ClimbProfileWithSteps>()
    }
    val customProfileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mCustomProfileWithSteps

    fun getProfileWithSteps(id: Long) {
        mCustomProfileWithSteps.addSource(profileDao.getCustomProfileWithSteps(id)) {
            mCustomProfileWithSteps.postValue(it)
        }
    }

    fun addStep(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.insertStep(ClimbStep(0, id, 0, 0))
        }
    }

    fun duplicateStep(id: Long, distance: Int, angle: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.insertStep(ClimbStep(0, id, distance, angle))
        }
    }

    fun updateStepDistance(distance: Int, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.updateCustomStepDistance(distance, id)
        }
    }

    fun updateStepAngle(angle: Int, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.updateCustomStepAngle(angle, id)
        }
    }

    fun deleteStep(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.deleteStep(id)
        }
    }
}
