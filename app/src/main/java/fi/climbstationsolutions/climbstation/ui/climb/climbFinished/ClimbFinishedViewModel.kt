package fi.climbstationsolutions.climbstation.ui.climb.climbFinished

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.SessionWithData

class ClimbFinishedViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.get(getApplication())
    private val sessionDao = database.sessionDao()

    private val liveDataMerger: MediatorLiveData<SessionWithData> =
        MediatorLiveData<SessionWithData>()

    val sessionWithData: LiveData<SessionWithData>
        get() = liveDataMerger

    private val mProfileWithSteps: MediatorLiveData<ClimbProfileWithSteps> by lazy {
        MediatorLiveData<ClimbProfileWithSteps>()
    }
    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfileWithSteps

    fun getSessionId(id: Long) {
        liveDataMerger.addSource(sessionDao.getSessionWithData(id)) {
            liveDataMerger.postValue(it)
        }
    }

    fun setProfile(profile: ClimbProfileWithSteps) {
        mProfileWithSteps.value = profile
    }

    private val mLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also { it.value = false }
    }
    val loading: LiveData<Boolean>
        get() = mLoading

    fun setLoading(value: Boolean) {
        mLoading.value = value
    }
}
