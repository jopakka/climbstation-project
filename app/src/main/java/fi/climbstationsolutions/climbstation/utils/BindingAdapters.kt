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
fun bindSessionTime(view: TextView, date: Date?) {

    var time: String = "no time"
    if (date != null) {
        time = DateFormat.getTimeInstance().format(date)
    }
    view.text = time
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