package fi.climbstationsolutions.climbstation.utils

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.Data
import fi.climbstationsolutions.climbstation.database.Session
import fi.climbstationsolutions.climbstation.database.SessionWithData
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
fun bindSessionLength(view: TextView, bundle: Bundle?) {

    val length: Int? = bundle?.getInt("length")
    var distance: Float? = 0f

    if (length != null) {
        distance = length / 1000f
    }

    view.text = "${distance ?: 0}mm"
}

@BindingAdapter("sessionCalories")
fun bindSessionCalories(view: TextView, bundle: Bundle?) {
    val length: Int? = bundle?.getInt("length")

    val calorieCounter = CalorieCounter()
    val calories = calorieCounter.countCalories(length?.toFloat() ?: 0f, 80f)
    view.text = "${calories}kcal"
}

@BindingAdapter("sessionSpeed")
fun bindSessionSpeed(view: TextView, bundle: Bundle?) {
    val speed: Int? = bundle?.getInt("speed")

    view.text = "${speed ?: 0}m/min"
}

// ClimbFinishedFragment
@BindingAdapter("climbFinishedTitle")
fun bindClimbFinishedTitle(view: TextView, title: String?) {
    view.text = view.context.getString(R.string.fragment_climb_finished_result_title, title)
}

@BindingAdapter("climbFinishedTitleLength")
fun bindClimbFinishedTitleLengthAndGoal(view: TextView, data: List<Data>?) {
    var distance: Int? = 0

    if (data?.size != null) {
        distance = data.last().totalDistance
    }

    view.text = "${distance}m"
}

@BindingAdapter("climbFinishedDuration")
fun bindClimbFinishedDuration(view: TextView, duration: Session?) {
    val startTime: Long? = duration?.createdAt?.time
    val endTime: Long? = duration?.endedAt?.time
    val result = startTime?.let { endTime?.minus(it) }

    val minutes = (result?.div(1000))?.div(60)
    val seconds = (result?.div(1000))?.rem(60)

    view.text = "00:${minutes ?: 0}:${seconds ?: 0}"
}

@BindingAdapter("climbFinishedDistance")
fun bindClimbFinishedDistance(view: TextView, data: List<Data>?) {
    var distance: Int? = 0

    if (data?.size != null) {
        distance = data.last().totalDistance
    }

    view.text = "${distance}m"
}

@BindingAdapter("climbFinishedCalories")
fun bindClimbFinishedCalories(view: TextView, data: List<Data>?) {
    var distance: Int? = 0

    if (data?.size != null) {
        distance = data.last().totalDistance
    }

    val calorieCounter = CalorieCounter()
    val calories = calorieCounter.countCalories(distance?.toFloat() ?: 0f, 80f)
    view.text = "${calories}kcal"
}

@BindingAdapter("climbFinishedAverageSpeed")
fun bindClimbFinishedAverageSpeed(view: TextView, sessionWithData: SessionWithData?) {

    sessionWithData ?: return
    val startTime: Long = sessionWithData.session.createdAt.time
    val endTime: Long = sessionWithData.session.endedAt?.time ?: 0L
    val result = endTime - startTime

    val seconds = result / 1000F

    val distance = sessionWithData.data.last().totalDistance / 1000F

    val avrgSpeed = (distance / seconds) * 60

    view.text = view.context.getString(R.string.fragment_climb_finished_speed_value, avrgSpeed)
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