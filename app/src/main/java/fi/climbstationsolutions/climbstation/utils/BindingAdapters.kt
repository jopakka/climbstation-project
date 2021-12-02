package fi.climbstationsolutions.climbstation.utils

import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.database.Session
import fi.climbstationsolutions.climbstation.database.SessionWithData
import java.text.DateFormat
import java.time.YearMonth
import java.util.*
import java.util.concurrent.TimeUnit


@BindingAdapter("stepsToDistance")
fun bindStepsToDistance(view: TextView, steps: List<ClimbStep>?) {
    val distance = if (steps?.isNotEmpty() == true) Calculators.calculateDistance(steps)
    else 0

    view.text = view.context.getString(R.string.distanceLong, distance.toFloat())
}

@BindingAdapter("stepsToDistanceShort")
fun bindStepsToDistanceShort(view: TextView, steps: List<ClimbStep>?) {
    val distance = if (steps?.isNotEmpty() == true) Calculators.calculateDistance(steps)
    else 0

    view.text = view.resources.getString(R.string.distanceShort, distance.toFloat())
}

@BindingAdapter("stepsToAngle")
fun bindStepsToAngle(view: TextView, steps: List<ClimbStep>?) {
    val avgAngle = if (steps?.isNotEmpty() == true) Calculators.averageAngleFromSteps(steps)
    else 0f

    view.text = view.context.getString(R.string.angleLong, avgAngle)
}

@BindingAdapter("speed")
fun bindSpeed(view: TextView, speed: Int?) {
    view.text = view.context.getString(R.string.speedShort, speed ?: 0)
}

@BindingAdapter("sessionTime")
fun bindSessionTimeLong(view: TextView, time: Long) {

    val hours = TimeUnit.MILLISECONDS.toHours(time)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)

    view.text = view.context.getString(R.string.stop_watch, hours, minutes, seconds)
}

@BindingAdapter("sessionTime")
fun bindSessionTime(view: TextView, sessionWithData: SessionWithData?) {
    sessionWithData ?: return

    val endTime = sessionWithData.session.endedAt?.time ?: 0L
    val startTime = sessionWithData.session.createdAt.time
    val time = if (endTime == 0L) 0L else endTime - startTime

    bindSessionTimeLong(view, time)
}

@BindingAdapter("sessionLength")
fun bindSessionLength(view: TextView, sessionWithData: SessionWithData?) {

    var distance = 0f

    if (sessionWithData?.data?.size != 0) {
        distance = sessionWithData?.data?.last()?.totalDistance?.div(1000f) ?: 0f
    }

    view.text = view.context.getString(R.string.float_single_decimal, distance)
}

@BindingAdapter("sessionCalories")
fun bindSessionCalories(view: TextView, sessionWithData: SessionWithData?) {
    var distance: Float? = 0f

    if (sessionWithData?.data?.size != 0) {
        distance = sessionWithData?.data?.last()?.totalDistance?.toFloat()
    }

    val calorieCounter = CalorieCounter()
    val calories = distance?.let { calorieCounter.countCalories(it, 80f) }
    view.text = view.context.getString(R.string.float_single_decimal, calories)
}

@BindingAdapter("sessionSpeed")
fun bindSessionSpeed(view: TextView, sessionWithData: SessionWithData?) {
    var speed = 0
    if (sessionWithData?.data?.size != 0) {
        speed = sessionWithData?.data?.last()?.speed ?: 0
    }

    view.text = "$speed"
}

@BindingAdapter("sessionDate")
fun bindSessionDate(view: TextView, date: Date) {
    val sessionDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)

    view.text = sessionDate
}

@BindingAdapter("climbFinishedDuration")
fun bindClimbFinishedDuration(view: TextView, duration: Session?) {
    val startTime: Long = duration?.createdAt?.time ?: 0L
    val endTime: Long = duration?.endedAt?.time ?: 0L
    val result = if (endTime == 0L) 0L else endTime - startTime

    val hours = TimeUnit.MILLISECONDS.toHours(result)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(result) % TimeUnit.HOURS.toMinutes(1)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(result) % TimeUnit.MINUTES.toSeconds(1)

    view.text = view.context.getString(R.string.stop_watch, hours, minutes, seconds)
}


@BindingAdapter("sessionAverageSpeed")
fun bindSessionAverageSpeed(view: TextView, sessionWithData: SessionWithData?) {

    sessionWithData ?: return
    val startTime: Long = sessionWithData.session.createdAt.time
    val endTime: Long = sessionWithData.session.endedAt?.time ?: 0L
    val result = endTime - startTime

    val seconds = result / 1000F

    val distance = sessionWithData.data.lastOrNull()?.totalDistance?.div(1000F)

    val avrgSpeed = (distance?.div(seconds))?.times(60)

    view.text = view.context.getString(R.string.float_single_decimal, avrgSpeed)
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


@BindingAdapter("allTimeDistance")
fun bindAllTimeDistance(view: TextView, distance: Int) {
    view.text = view.context.getString(R.string.distanceShort, distance.div(1000f))
}

@BindingAdapter("infoPopupTitle")
fun bindInfoPopupTitle(view: TextView, title: String) {
    when (title) {
        "How to climb" -> {
            view.text = view.context.getString(R.string.info_popup_title_climb)
        }
        "How to connect to ClimbStation machine" -> {
            view.text = view.context.getString(R.string.info_popup_title_connect)
        }
        "How to create custom climbing profiles" -> {
            view.text = view.context.getString(R.string.info_popup_title_create_custom_climb_profile)
        }
        else -> {
            view.text = "error"
            Log.d("bindInfoPopupTitle", "no such title: $title")
        }
    }
}

@BindingAdapter("infoPopupInstructions")
fun bindInfoPopupInstructions(view: TextView, title: String) {
    when (title) {
        "How to climb" -> {
            view.text = view.context.getString(R.string.info_popup_instructions_climb)
        }
        "How to connect to ClimbStation machine" -> {
            view.text = view.context.getString(R.string.info_popup_instructions_connect)
        }
        "How to create custom climbing profiles" -> {
            view.text = view.context.getString(R.string.info_popup_instructions_create_custom_profile)
        }
        else -> {
            view.text = "error"
            Log.d("bindInfoPopupTitle", "no such instructions: $title")
        }
    }
}

@BindingAdapter("yearMonth")
fun bindYearMonth(view: TextView, yearMonth: YearMonth?) {
    view.text = if (yearMonth == null) ""
    else view.context.getString(
        R.string.session_list_year_month,
        yearMonth.month.name,
        yearMonth.year
    )
}

@BindingAdapter("sessionCount")
fun bindSessionCount(view: TextView, sessionCount: Int?) {
    val count = sessionCount ?: 0
    val res = view.context.resources
    view.text = res.getQuantityString(R.plurals.session_list_session_count, count, count)
}

@BindingAdapter("stepsCount")
fun bindStepsCount(view: TextView, steps: List<ClimbStep>?) {
    if (steps != null) {
        view.text = "${steps.count()}"
    }
}

@BindingAdapter("stepNumber")
fun bindStepNumber(view: TextView, step: ClimbStep) {
    view.text = "${step.id}"
}

@BindingAdapter("stepDistance")
fun bindStepDistance(view: TextInputEditText, step: ClimbStep?) {
    view.text = Editable.Factory.getInstance().newEditable((step?.distance ?: 0).toString())
}

@BindingAdapter("stepAngle")
fun bindStepAngle(view: TextInputEditText, step: ClimbStep?) {
    view.text = Editable.Factory.getInstance().newEditable((step?.angle ?: 0).toString())
}

@BindingAdapter("filterMonth", "filterYear")
fun bindFilterMonthYear(view: TextView, month: String?, year: Int?) {
    view.visibility = if (month == null || year == null) View.GONE
    else {
        val monthCapitalize = month.lowercase(Locale.getDefault()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
        view.text = view.context.getString(R.string.filter_month_year, monthCapitalize, year)
        View.VISIBLE
    }

}