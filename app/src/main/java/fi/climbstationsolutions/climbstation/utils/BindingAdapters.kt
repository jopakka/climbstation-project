package fi.climbstationsolutions.climbstation.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.Data
import fi.climbstationsolutions.climbstation.database.Session
import fi.climbstationsolutions.climbstation.network.profile.Step
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("stepsToDistance")
fun bindStepsToDistance(view: TextView, steps: List<Step>?) {
    var distance = 0
    if (steps != null) {
        for (i in steps) {
            distance += i.distance
        }
    }
    view.text = view.context.getString(R.string.distance, distance)
}

@BindingAdapter("stepsToAngle")
fun bindStepsToAngle(view: TextView, steps: List<Step>?) {
    var angle = 0
    if (steps != null) {
        for (i in steps) {
            angle += i.angle
        }
    }
    angle /= steps?.size ?: 1

    view.text = view.context.getString(R.string.angle, angle)
}

@BindingAdapter("stepsToSpeed")
fun bindStepsToSpeed(view: TextView, steps: List<Step>?) {
    val speed = 0

    view.text = view.context.getString(R.string.speed, speed)
}

@BindingAdapter("sessionTime")
fun bindSessionTime(view: TextView, time: Long) {

    val minutes = (time / 1000) / 60
    val seconds = (time / 1000) % 60

    view.text = "00:${minutes}:${seconds}"
}

@BindingAdapter("sessionLength")
fun bindSessionLength(view: TextView, data: List<Data>?) {

    var distance: Int? = 0

    if (data?.size != 0) {
        distance = data?.last()?.totalDistance
    }

    view.text = "${distance}"
}

@BindingAdapter("sessionCalories")
fun bindSessionCalories(view: TextView, data: List<Data>?) {
    var distance: Int? = 0

    if (data?.size != 0) {
        distance = data?.last()?.totalDistance
    }

    val calorieCounter = CalorieCounter()
    val calories = calorieCounter.countCalories(distance?.toFloat() ?: 0f, 80f)
    view.text = "$calories"
}

@BindingAdapter("sessionSpeed")
fun bindSessionSpeed(view: TextView, data: List<Data>?) {
    var speed: Int? = 0
    if (data?.size != 0) {
        speed = data?.last()?.speed
    }

    view.text = "$speed"
}

// ClimbFinishedFragment
@BindingAdapter("climbFinishedTitle")
fun bindClimbFinishedTitle(view: TextView, title: String?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_result_title, title)
}

@BindingAdapter(value = ["climbFinishedTitleLength", "climbFinishedTitleGoalLength"], requireAll = false)
fun bindClimbFinishedTitleLengthAndGoal(view: TextView, climbFinishedTitleLength: Float?, climbFinishedGoalLength: Float?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_result_detail, climbFinishedTitleLength, climbFinishedGoalLength)
}

@BindingAdapter("climbFinishedDifficultyStart")
fun bindClimbFinishedDifficultyStart(view: TextView, difficultyStart: String?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_difficulty_start, difficultyStart)
}

@BindingAdapter("climbFinishedDifficultyEnd")
fun bindClimbFinishedDifficultyEnd(view: TextView, difficultyEnd: String?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_difficulty_end, difficultyEnd)
}

@BindingAdapter("climbFinishedMode")
fun bindClimbFinishedMode(view: TextView, mode: String?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_mode, mode)
}

@BindingAdapter("climbFinishedDuration")
fun bindClimbFinishedDuration(view: TextView, duration: String?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_time_value, duration)
}

@BindingAdapter("climbFinishedDistance")
fun bindClimbFinishedDistance(view: TextView, distance: Float?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_length_value, distance)
}

@BindingAdapter("climbFinishedCalories")
fun bindClimbFinishedCalories(view: TextView, calories: Float?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_calories_value, calories)
}

@BindingAdapter("climbFinishedAverageSpeed")
fun bindClimbFinishedAverageSpeed(view: TextView, averageSpeed: Float?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_speed_value, averageSpeed)
}

// Settings fragment
@BindingAdapter("settingsUserWeightDisplay")
fun bindSettingsUserWeightDisplay(view: TextView, userWeight: Float?) {
    view.text = view.context.getString(R.string.fragment_settings_weight, userWeight)
}

// Adjust fragment
@BindingAdapter("adjustSpeed")
fun bindAdjustSpeed(view: TextView, speed: Int?) {
    view.text = view.context.getString(R.string.fragment_adjust_speed, speed)
}