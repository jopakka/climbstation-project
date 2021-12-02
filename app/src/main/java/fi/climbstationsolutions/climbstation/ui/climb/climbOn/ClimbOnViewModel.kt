package fi.climbstationsolutions.climbstation.ui.climb.climbOn

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class ClimbOnViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    val sessionWithData = sessionDao.getLastSessionWithData()

    private val mProfileWithSteps: MediatorLiveData<ClimbProfileWithSteps> by lazy {
        MediatorLiveData<ClimbProfileWithSteps>()
    }
    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfileWithSteps

    fun setProfile(profileWithSteps: ClimbProfileWithSteps) {
        mProfileWithSteps.value = profileWithSteps
    }

    private val mTimer: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }

    val timer: LiveData<Long>
        get() = mTimer

    private var useTimer = true

    fun startTimer() {
        viewModelScope.launch(Dispatchers.Default) {
            useTimer = true
            while (useTimer) {
                val startTime = sessionWithData.value?.session?.createdAt?.time
                val result = startTime?.let {
                    Calendar.getInstance().timeInMillis - startTime
                }
                mTimer.postValue(result)
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        useTimer = false
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
