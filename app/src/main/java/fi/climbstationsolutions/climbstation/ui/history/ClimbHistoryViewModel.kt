package fi.climbstationsolutions.climbstation.ui.history

import android.app.Application
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.SessionWithData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClimbHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.get(getApplication())
    private val sessionDao = db.sessionDao()
    private val profileDao = db.profileDao()

    private val liveDataMerger: MediatorLiveData<SessionWithData> =
        MediatorLiveData<SessionWithData>()

    val sessionWithData: LiveData<SessionWithData>
        get() = liveDataMerger

    private val mProfileWithSteps: MediatorLiveData<ClimbProfileWithSteps> by lazy {
        MediatorLiveData<ClimbProfileWithSteps>()
    }
    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfileWithSteps

    fun getSession(id: Long) {
        liveDataMerger.addSource(sessionDao.getSessionWithData(id)) {
            liveDataMerger.postValue(it)
        }
    }

    init {
        mProfileWithSteps.addSource(sessionWithData) {
            viewModelScope.launch(Dispatchers.IO) {
                mProfileWithSteps.postValue(profileDao.getProfileWithSteps(it.session.profileId))
            }
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
}