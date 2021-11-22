package fi.climbstationsolutions.climbstation.ui.climb.climbFinished

import android.app.Application
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.SessionWithData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ClimbFinishedViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.get(getApplication())
    private val sessionDao = database.sessionDao()
    private val profileDao = database.profileDao()

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

    private val mLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also { it.value = false }
    }
    val loading: LiveData<Boolean>
        get() = mLoading

    fun setLoading(value: Boolean) {
        mLoading.value = value
    }

    init {
        mProfileWithSteps.addSource(sessionWithData) {
            viewModelScope.launch(Dispatchers.IO) {
                mProfileWithSteps.postValue(profileDao.getProfileWithSteps(it.session.profileId))
            }
        }
    }
}