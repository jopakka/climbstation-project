package fi.climbstationsolutions.climbstation.ui.viewmodels

import android.content.Context
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.ui.climb.climbOnfragment.ClimbOnViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClimbFinishedViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    private val mSession: MutableLiveData<SessionWithData> by lazy {
        MutableLiveData<SessionWithData>()
    }
    val session: LiveData<SessionWithData>
        get() = mSession

    fun addSessionId(id: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            mSession.value = sessionDao.getSessionWithData(id)
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