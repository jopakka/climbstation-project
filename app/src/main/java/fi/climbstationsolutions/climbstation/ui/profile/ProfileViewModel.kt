package fi.climbstationsolutions.climbstation.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.climbstationsolutions.climbstation.database.AppDatabase

class ProfileViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    val allSessions = sessionDao.getAllSessionsWithData()

    val allTimeDistance = sessionDao.getAllTimeDistance()
    val allTimeDuration = sessionDao.getAllTimeDuration()
    val sevenDayDistance = sessionDao.getSevenDayDistance()
    val sevenDateDuration = sessionDao.getSevenDayDuration()
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