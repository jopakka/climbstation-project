package fi.climbstationsolutions.climbstation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClimbFinishedViewModel: ViewModel() {

    private val _title = MutableLiveData<String>()
    val selectedTitle: LiveData<String> = _title

    private val _titleLength = MutableLiveData<Float>()
    val selectedTitleLength: LiveData<Float> = _titleLength

    private val _selectedTitleGoalLength = MutableLiveData<Float>()
    val selectedTitleGoalLength: LiveData<Float> = _selectedTitleGoalLength

    private val _selectedDifficultyStart = MutableLiveData<String>()
    val selectedDifficultyStart: LiveData<String> = _selectedDifficultyStart

    private val _selectedDifficultyEnd = MutableLiveData<String>()
    val selectedDifficultyEnd: LiveData<String> = _selectedDifficultyEnd

    private val _selectedMode = MutableLiveData<String>()
    val selectedMode: LiveData<String> = _selectedMode

    private val _duration = MutableLiveData<String>()
    val duration: LiveData<String> = _duration

    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float> = _distance

    private val _calories = MutableLiveData<Float>()
    val calories: LiveData<Float> = _calories

    private val _averageSpeed = MutableLiveData<Float>()
    val averageSpeed: LiveData<Float> = _averageSpeed

    fun setSelectedTitle(title: String) {
        _title.value = title
    }

    fun setSelectedTitleLength(length: Float) {
        _titleLength.value = length
    }

    fun setSelectedTitleGoalLength(length: Float) {
        _selectedTitleGoalLength.value = length
    }

    fun setSelectedDifficultyStart(difficulty: String) {
        _selectedDifficultyStart.value = difficulty
    }

    fun setSelectedDifficultyEnd(difficulty: String) {
        _selectedDifficultyEnd.value = difficulty
    }

    fun setSelectedMode(mode: String) {
        _selectedMode.value = mode
    }

    // currently string, but might be changed later
    fun setDuration(duration: String) {
        _duration.value = duration
    }

    fun setDistance(distance: Float) {
        _distance.value = distance
    }

    fun setCalories(calories: Float) {
        _calories.value = calories
    }

    fun setAverageSpeed(averageSpeed: Float) {
        _averageSpeed.value = averageSpeed
    }
}