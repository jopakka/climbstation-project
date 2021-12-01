package fi.climbstationsolutions.climbstation.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.climbstationsolutions.climbstation.database.ClimbProfile
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.ClimbStep

class AdjustViewModel : ViewModel() {
    private val profileId = 1000L
    private val mAdjustMutableLiveData: MutableLiveData<Timer> = MutableLiveData(Timer())

    private val mProfileWithSteps: MutableLiveData<ClimbProfileWithSteps> =
        MutableLiveData<ClimbProfileWithSteps>()

    val profileWithSteps: LiveData<ClimbProfileWithSteps>
        get() = mProfileWithSteps

    private val mClimbProfile: ClimbProfile = ClimbProfile(profileId, "Adjusted", 10, false)

    val climbProfile: ClimbProfile
        get() = mClimbProfile

    private val mClimbSteps: List<ClimbStep> = listOf(ClimbStep(1, profileId, 0, 0))

    val climbSteps: List<ClimbStep>
        get() = mClimbSteps

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

    private val mLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also { it.value = false }
    }
    val loading: LiveData<Boolean>
        get() = mLoading

    fun setMinute(minute: Int) {
        mAdjustMutableLiveData.value?.minute = minute
        Log.d("setMinute", "value: ${mAdjustMutableLiveData.value}")

    }

    fun setSecond(second: Int) {
        mAdjustMutableLiveData.value?.second = second
        Log.d("setSecond", "value: ${mAdjustMutableLiveData.value}")
    }

    fun setAngle(angle: Int) {
        mClimbSteps[0].angle = angle
        Log.d("setAngle", "value: ${mClimbSteps}")
    }

    fun setLength(distance: Int) {
        mClimbSteps[0].distance = distance
        Log.d("setLength", "value: ${mClimbSteps}")
    }

    fun getValues(): ClimbProfileWithSteps? {
        Log.d("Timer", "data: ${mAdjustMutableLiveData.value}")
        Log.d("ClimbSteps", "data: ${mClimbSteps}")
        return profileWithSteps.value
    }

    fun setLoading(value: Boolean) {
        mLoading.value = value
    }

    fun setClimbProfileWithSteps() {
        val testProfile = ClimbProfile(profileId, "Adjusted", 10, false)
        val testSteps = listOf(ClimbStep(1, profileId, 0, 0))
        mProfileWithSteps.value = ClimbProfileWithSteps(climbProfile, climbSteps)
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