package fi.climbstationsolutions.climbstation.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.database.Session
import fi.climbstationsolutions.climbstation.database.SessionWithData
import java.util.concurrent.TimeUnit


@BindingAdapter("stepsToDistance")
fun bindStepsToDistance(view: TextView, steps: List<ClimbStep>?) {
    val distance = if(steps?.isNotEmpty() == true) Calculators.calculateDistance(steps)
    else 0

    view.text = view.context.getString(R.string.distanceLong, distance)
}

@BindingAdapter("stepsToDistanceShort")
fun bindStepsToDistanceShort(view: TextView, steps: List<ClimbStep>?) {
    val distance = if(steps?.isNotEmpty() == true) Calculators.calculateDistance(steps)
    else 0

    view.text = view.resources.getString(R.string.distanceShort, distance.toFloat())
}

@BindingAdapter("stepsToAngle")
fun bindStepsToAngle(view: TextView, steps: List<ClimbStep>?) {
    var angle = 0
    if (steps != null) {
        for (i in steps) {
            angle += i.angle
        }
    }
    angle /= steps?.size ?: 1

    view.text = view.context.getString(R.string.angleLong, angle)
}

@BindingAdapter("speed")
fun bindSpeed(view: TextView, speed: Int?) {
    view.text = view.context.getString(R.string.speedShort, speed ?: 0)
}

@BindingAdapter("sessionTime")
fun bindSessionTime(view: TextView, time: Long) {

    val hours = TimeUnit.MILLISECONDS.toHours(time)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)

    view.text = view.context.getString(R.string.stop_watch, hours, minutes, seconds)
}

@BindingAdapter("sessionLength")
fun bindSessionLength(view: TextView, sessionWithData: SessionWithData?) {

    var distance: Float? = 0f

    if (sessionWithData?.data?.size != 0) {
        distance = sessionWithData?.data?.last()?.totalDistance?.div(1000f)
    }

    view.text = view.context.getString(R.string.distanceShort, distance)
}

@BindingAdapter("sessionCalories")
fun bindSessionCalories(view: TextView, sessionWithData: SessionWithData?) {
    var distance: Float? = 0f

    if (sessionWithData?.data?.size != 0) {
        distance = sessionWithData?.data?.last()?.totalDistance?.toFloat()
    }

    val calorieCounter = CalorieCounter()
    val calories = distance?.let { calorieCounter.countCalories(it, 80f) }
    view.text = view.context.getString(R.string.caloriesShort, calories)
}

@BindingAdapter("sessionSpeed")
fun bindSessionSpeed(view: TextView, sessionWithData: SessionWithData?) {
    var speed: Int? = 0
    if (sessionWithData?.data?.size != 0) {
        speed = sessionWithData?.data?.last()?.speed
    }

    view.text = view.context.getString(R.string.speedShort, speed)
}

@BindingAdapter("climbFinishedDuration")
fun bindClimbFinishedDuration(view: TextView, duration: Session?) {
    val startTime: Long = duration?.createdAt?.time ?: 0L
    val endTime: Long = duration?.endedAt?.time ?: 0L
    val result = endTime - startTime

    val timer = String.format(
        "%02d:%02d:%02d", result.let { TimeUnit.MILLISECONDS.toHours(it) },
        result.let { TimeUnit.MILLISECONDS.toMinutes(it) }.rem(TimeUnit.HOURS.toMinutes(1)),
        result.let { TimeUnit.MILLISECONDS.toSeconds(it) }.rem(TimeUnit.MINUTES.toSeconds(1))
    )

    view.text = timer
}


@BindingAdapter("sessionAverageSpeed")
fun bindSessionAverageSpeed(view: TextView, sessionWithData: SessionWithData?) {

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

@BindingAdapter("stepsAverageAngle")
fun bindStepsAverageAngle(view: TextView, steps: List<ClimbStep>?) {
    val avgAngle = if (steps?.isNotEmpty() == true) Calculators.averageAngleFromSteps(steps)
    else 0f

    view.text = view.resources.getString(R.string.angleShort, avgAngle)
}
