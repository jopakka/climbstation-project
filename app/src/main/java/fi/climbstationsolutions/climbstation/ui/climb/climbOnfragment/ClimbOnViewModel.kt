package fi.climbstationsolutions.climbstation.ui.climb.climbOnfragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.network.profile.Profile
import fi.climbstationsolutions.climbstation.utils.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.LiveData
import fi.climbstationsolutions.climbstation.ui.viewmodels.ClimbFinishedViewModel
import kotlinx.coroutines.withContext


class ClimbOnViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    private val mBundle: MutableLiveData<Bundle> by lazy {
        MutableLiveData<Bundle>()
    }

    val bundle: LiveData<Bundle>
        get() = mBundle

    fun addBundle(bundle: Bundle) {
        mBundle.postValue(bundle)
    }

    val timer: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }

    fun startTimer() {
        viewModelScope.launch(Dispatchers.Default) {
            val startTime = Calendar.getInstance().timeInMillis

            while (true) {
                delay(1000)
                val result = Calendar.getInstance().timeInMillis - startTime
                timer.postValue(result)
            }
        }
    }

    suspend fun getSessionId(): Long = withContext(Dispatchers.IO) {
        val sessionId = sessionDao.getLastSessionWithData()
        return@withContext sessionId.session.id
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
