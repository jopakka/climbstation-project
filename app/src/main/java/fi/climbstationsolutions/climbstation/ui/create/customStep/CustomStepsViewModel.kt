package fi.climbstationsolutions.climbstation.ui.create.customStep

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
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
