package fi.climbstationsolutions.climbstation.ui.profile

import android.content.Context
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    val allTimeDistance = sessionDao.getAllTimeDistance()
    val allTimeDuration = sessionDao.getAllTimeDuration()
    val sevenDayDistance = sessionDao.getSevenDayDistance()
    val sevenDateDuration = sessionDao.getSevenDayDuration()

    private val mFilteredSessions: MutableLiveData<List<SessionWithData>> by lazy {
        MutableLiveData<List<SessionWithData>>()
    }
    val filteredSessions: LiveData<List<SessionWithData>>
        get() = mFilteredSessions

    init {
        showAllSessions()
    }

    fun filterList(start: Date, end: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessions = sessionDao.getSessionWithDataBetween(start, end)
            mFilteredSessions.postValue(sessions)
        }
    }

    fun showAllSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            mFilteredSessions.postValue(sessionDao.getAllSessionsWithData())
        }
    }
}

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}