package fi.climbstationsolutions.climbstation.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdjustViewModel: ViewModel() {
    private val mAdjustMutableLiveData: MutableLiveData<AdjustData> = MutableLiveData(AdjustData())

    // amount of numbers in angle picker
    private val angleAmount = 45
    val angleNumbers: List<Int>
        get() {
            val numbersMutableList = mutableListOf(0)
            for(i in -15..angleAmount + 5) {
                numbersMutableList.add(i)
            }
            return numbersMutableList
        }

    // amount of numbers in length picker
    private val lengthAmount = 100
    val lengthNumbers: List<Int>
        get() {
            val numbersMutableList = mutableListOf(0)
            for(i in 1..lengthAmount + 5) {
                numbersMutableList.add(i)
            }
            return numbersMutableList
        }

    fun setMinute(minute: Int) {
        mAdjustMutableLiveData.value?.minute = minute
        Log.d("setMinute","value: ${mAdjustMutableLiveData.value}")
    }

    fun setSecond(second: Int) {
        mAdjustMutableLiveData.value?.second = second
        Log.d("setSecond","value: ${mAdjustMutableLiveData.value}")
    }

    fun setAngle(angle: Int) {
        mAdjustMutableLiveData.value?.angle = angle
        Log.d("setAngle","value: ${mAdjustMutableLiveData.value}")
    }

    fun setLength(length: Int) {
        mAdjustMutableLiveData.value?.length = length
        Log.d("setLength","value: ${mAdjustMutableLiveData.value}")
    }

    fun getValues(): AdjustData? {
        Log.d("AdjustData", "data: ${mAdjustMutableLiveData.value}")
        return mAdjustMutableLiveData.value
    }
}

data class AdjustData(var minute: Int = 0, var second: Int = 0, var angle: Int = 0, var length: Int = 0)