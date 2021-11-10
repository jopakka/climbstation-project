package fi.climbstationsolutions.climbstation.ui.climb.climbOnfragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.network.profile.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ClimbOnViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    val sessionWithData = sessionDao.getLastSessionWithData()
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