package fi.climbstationsolutions.climbstation.ui.climb.manualStart

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.ClimbProfile
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.ClimbStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Oskar Wiiala
 * @author Patrik Pölkki
 * View model for ManualStartFragment
 * Handles setting all necessary variables such as timer, distance etc to be used in preparing climbing session
 */
class ManualStartViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.get(getApplication())
    private val profileDao = db.profileDao()

    private val mAdjustMutableLiveData: MutableLiveData<Timer> = MutableLiveData(Timer())
    val timer: LiveData<Timer>
        get() = mAdjustMutableLiveData

    private val mProfileWithSteps: MutableLiveData<ClimbProfileWithSteps> =
        MutableLiveData<ClimbProfileWithSteps>()

    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfileWithSteps

    private var climbAngle = 0
    private var climbLength = 0

    // amount of numbers in angle picker
    private val angleAmount = 45
    val angleNumbers: List<Int>
        get() {
            val numbersMutableList = mutableListOf(0)
            for (i in -15..angleAmount + 5) {
                numbersMutableList.add(i)
            }
            return numbersMutableList
        }

    // amount of numbers in length picker
    private val lengthAmount = 100
    val lengthNumbers: List<Int>
        get() {
            val numbersMutableList = mutableListOf(0)
            for (i in 1..lengthAmount + 5) {
                numbersMutableList.add(i)
            }
            return numbersMutableList
        }

    fun setMinute(minute: Int) {
        mAdjustMutableLiveData.value?.minute = minute
    }

    fun setSecond(second: Int) {
        mAdjustMutableLiveData.value?.second = second
    }

    fun setAngle(angle: Int) {
        climbAngle = angle
    }

    fun setLength(distance: Int) {
        climbLength = distance
    }

    fun getClimbProfileWithSteps(): ClimbProfileWithSteps? {
        return profileWithSteps.value
    }

    fun setClimbProfileWithSteps() {
        viewModelScope.launch(Dispatchers.IO) {
            val profId = profileDao.insertProfile(ClimbProfile(0, "Manual", manual = true))
            profileDao.insertStep(ClimbStep(0, profId, climbLength, climbAngle))
            val profile = profileDao.getProfileWithSteps(profId)
            mProfileWithSteps.postValue(profile)
        }
    }

    fun getTime(): Int? {
        val minute = mAdjustMutableLiveData.value?.minute
        val second = mAdjustMutableLiveData.value?.second
        val timeMillis = second?.let { (minute?.times(60))?.plus(it)?.times(1000) }
        return timeMillis
    }
}

data class Timer(
    var minute: Int = 0,
    var second: Int = 0,
)