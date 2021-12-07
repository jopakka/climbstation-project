package fi.climbstationsolutions.climbstation.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.climbstationsolutions.climbstation.database.ClimbProfile
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.ClimbStep

class ManualStartViewModel : ViewModel() {
    private val profileId = 1000L
    private val mAdjustMutableLiveData: MutableLiveData<Timer> = MutableLiveData(Timer())
    val timer: LiveData<Timer>
        get() = mAdjustMutableLiveData

    private val mProfileWithSteps: MutableLiveData<ClimbProfileWithSteps> =
        MutableLiveData<ClimbProfileWithSteps>()

    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfileWithSteps

    var climbAngle = 0
        private set
    var climbLength = 0
        private set

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
//        Log.d("setMinute", "value: ${mAdjustMutableLiveData.value}")

    }

    fun setSecond(second: Int) {
        mAdjustMutableLiveData.value?.second = second
//        Log.d("setSecond", "value: ${mAdjustMutableLiveData.value}")
    }

    fun setAngle(angle: Int) {
        climbAngle = angle
        Log.d("setAngle", "angle: $climbAngle ")
    }

    fun setLength(distance: Int) {
        climbLength = distance
        Log.d("setLength", "length: $climbLength ")
    }

    fun getClimbProfileWithSteps(): ClimbProfileWithSteps? {
        return profileWithSteps.value
    }

    fun setClimbProfileWithSteps() {
//        val testProfile = ClimbProfile(profileId, "Manual", 10)
//        val testSteps = listOf(ClimbStep(1, profileId, 0, 0))
        mProfileWithSteps.value = ClimbProfileWithSteps(
            ClimbProfile(profileId, "Manual", 10),
            listOf(ClimbStep(1, profileId, climbLength, climbAngle))
        )
        Log.d("setClimbProfileWithSteps", "value: ${profileWithSteps.value}")
    }

    fun getTime(): Int? {
        val minute = mAdjustMutableLiveData.value?.minute
        val second = mAdjustMutableLiveData.value?.second
        val timeMillis = second?.let { (minute?.times(60))?.plus(it)?.times(1000) }
        Log.d("getTime", "timeMillis: $timeMillis")
        return timeMillis
    }
}

data class Timer(
    var minute: Int = 0,
    var second: Int = 0,
)