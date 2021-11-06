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