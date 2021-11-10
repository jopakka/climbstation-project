package fi.climbstationsolutions.climbstation.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.network.profile.Step

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

@BindingAdapter("settingsUserWeightDisplay")
fun bindSettingsUserWeightDisplay(view: TextView, userWeight: Float?) {
    view.text = view.context.getString(R.string.fragment_settings_weight, userWeight)
}