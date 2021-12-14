package fi.climbstationsolutions.climbstation.ui.create.customProfile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CustomProfileViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.get(getApplication())
    private val profileDao = db.profileDao()

    val customProfiles = profileDao.getAllCustomProfiles()

    fun addCustomProfile(profileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentDate = Calendar.getInstance().time
            profileDao.insertProfile(ClimbProfile(0, profileName, createdAt = currentDate, isDefault = false))
        }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileDao.deleteProfile(id)
        }
    }
}