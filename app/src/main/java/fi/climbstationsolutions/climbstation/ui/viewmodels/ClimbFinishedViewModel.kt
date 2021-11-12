package fi.climbstationsolutions.climbstation.ui.viewmodels

import android.content.Context
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.ui.climb.climbOnfragment.ClimbOnViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.MediatorLiveData


class ClimbFinishedViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    private val liveDataMerger: MediatorLiveData<SessionWithData> =
        MediatorLiveData<SessionWithData>()

    val sessionWithData: LiveData<SessionWithData>
        get() = liveDataMerger

    fun getSessionId(id: Long) {
        liveDataMerger.addSource(sessionDao.getSessionWithData(id)) { value: SessionWithData? ->
            liveDataMerger.setValue(
                value
            )
        }
    }
}

class ClimbFinishedViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClimbFinishedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClimbFinishedViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}