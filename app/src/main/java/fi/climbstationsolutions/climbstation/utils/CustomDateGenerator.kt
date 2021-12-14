package fi.climbstationsolutions.climbstation.utils

import java.time.ZoneId
import java.util.*

/**
 * @author Oskar Wiiala
 * Generates some custom dates used with the graph
 */
object CustomDateGenerator {
    fun getToday(): String {
        val date = Date()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val month = localDate.month.toString().take(3)
        val day = localDate.dayOfMonth
        return if (day < 10) "0$day $month"
        else "$day $month"
    }

    fun getThisWeek(): String {
        val date = Date()
        val cal = Calendar.getInstance()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val year = localDate.year
        val month = localDate.month.toString().take(3)
        val week = cal[Calendar.WEEK_OF_YEAR]

        cal.clear()
        cal.set(Calendar.WEEK_OF_YEAR, week)
        cal.set(Calendar.YEAR, year)
        var firstDayOfWeek = cal[Calendar.DAY_OF_MONTH].toString()
        if (firstDayOfWeek.toInt() < 10) firstDayOfWeek = "0$firstDayOfWeek"

        cal.clear()
        cal.set(Calendar.WEEK_OF_YEAR, week + 1)
        cal.set(Calendar.YEAR, year)
        var lastDayOfWeek = (cal[Calendar.DAY_OF_MONTH] - 1).toString()
        if (lastDayOfWeek.toInt() < 10) lastDayOfWeek = "0$lastDayOfWeek"
        return "$firstDayOfWeek - $lastDayOfWeek $month"
    }

    fun getThisMonth(): String {
        val date = Date()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val month = localDate.month.toString()
        return month.substring(0, 1).uppercase() + month.substring(1).lowercase()
    }

    fun getThisYear(): String {
        val date = Date()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.year.toString()
    }
}