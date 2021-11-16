package fi.climbstationsolutions.climbstation.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.BodyWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivityViewModel(context: Context) : ViewModel() {
    private val db = AppDatabase.get(context)
    private val settingsDao = db.settingsDao()
    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private var userBodyWeightDefault: Float = 70.00F

    fun setWeight() {
        ioScope.launch {
            val userWeight = settingsDao.getBodyWeightById(1)
            if (userWeight == null) {
                settingsDao.insertUserBodyWeight(BodyWeight(1, userBodyWeightDefault))
            }
        }
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