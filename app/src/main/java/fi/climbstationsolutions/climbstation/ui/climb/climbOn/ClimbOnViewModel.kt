package fi.climbstationsolutions.climbstation.ui.climb.climbOn

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.LiveData


class ClimbOnViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    private val liveDataMerger: MediatorLiveData<SessionWithData> =
        MediatorLiveData<SessionWithData>()

    val sessionWithData: LiveData<SessionWithData>
        get() = liveDataMerger

    fun getSessionById(id: Long) {
        liveDataMerger.addSource(sessionDao.getSessionWithData(id)) {
            liveDataMerger.setValue(it)
        }
    }

    fun getLastSession() {
        liveDataMerger.addSource(sessionDao.getLastSessionWithData()) {
            liveDataMerger.setValue(it)
        }
    }

    private val mTimer: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }

    val timer: LiveData<Long>
        get() = mTimer

    fun startTimer() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(1000)
                val startTime = sessionWithData.value?.session?.createdAt?.time
                val result = startTime?.let {
                    Calendar.getInstance().timeInMillis - startTime
                }
                Log.d("Result", result.toString())
                mTimer.postValue(result)
            }
        }
    }
}

class ClimbOnViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClimbOnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClimbOnViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
