package fi.climbstationsolutions.climbstation.ui.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.BodyWeight
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.set
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SPEED_PREF_NAME
import kotlinx.coroutines.*

class MainActivityViewModel(context: Context) : ViewModel() {
    private val db = AppDatabase.get(context)
    private val settingsDao = db.settingsDao()
    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private var userBodyWeightDefault: Float = 70.00F

    fun setWeight(inputWeight: Float? = null, callBack: () -> Unit = {}) {
        ioScope.launch {
            val userWeight = settingsDao.getBodyWeightById(1)
            if (userWeight == null) {
                settingsDao.insertUserBodyWeight(BodyWeight(1, userBodyWeightDefault))
            }
            if(inputWeight != null) {
                if(userWeight != null) {
                    settingsDao.updateUserBodyWeight(inputWeight)
                }
            }
            withContext(Dispatchers.Main) {
                callBack()
            }
        }
    }

    fun setSpeed(context: Context, speed: Int? = null, callBack: () -> Unit = {}) {
        if(speed != null) {
            val prefs = PreferenceHelper.customPrefs(context, PREF_NAME)
            prefs[SPEED_PREF_NAME] = speed
            callBack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeight(): Float? = withContext(Dispatchers.IO){
            val userWeight = settingsDao.getBodyWeightById(1)
        return@withContext userWeight?.weight
    }

    fun getSpeed(context: Context): String {
        val prefs = PreferenceHelper.customPrefs(context, PREF_NAME)
        val speedString: String = when(prefs[SPEED_PREF_NAME, -1]) {
            4 -> "Slow"
            8 -> "Normal"
            12 -> "Fast"
            16 -> "Very fast"
            else -> "Normal"
        }
        return speedString
    }
}

class MainActivityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}