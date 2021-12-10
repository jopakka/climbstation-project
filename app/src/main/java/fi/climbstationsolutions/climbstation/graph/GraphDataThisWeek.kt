package fi.climbstationsolutions.climbstation.graph

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.utils.CalorieCounter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class GraphDataThisWeek(context: Context) {
    // This holds data to be inserted into DataPoints
    private val dayList: MutableList<Double> = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createGraphData(selectedVariable: String): BarGraphSeries<DataPoint> = withContext(
        Dispatchers.IO
    ) {
        val date = Date()
        val calendarCurrent = Calendar.getInstance()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val year = localDate.year
        val month = localDate.monthValue
        val week = calendarCurrent[Calendar.WEEK_OF_YEAR]

        calendarCurrent.clear()
        calendarCurrent.set(Calendar.WEEK_OF_YEAR, week);
        calendarCurrent.set(Calendar.YEAR, year);
        val day = calendarCurrent[Calendar.DAY_OF_MONTH]

        Log.d("GraphDataThisWeek", "currentYear: $year, currentWeek: $week, firstDayOfWeek: $day")

        val beginningOfWeek = LocalDateTime.of(year, month, day, 0, 0)
        val weekSelected = Date.from(beginningOfWeek.toInstant(ZoneOffset.UTC))
        val nextWeek = Date.from(beginningOfWeek.plusWeeks(1).toInstant(ZoneOffset.UTC))

        val sessionsThisWeek: List<SessionWithData> =
            sessionDao.getSessionWithDataBetween(weekSelected, nextWeek)

        val calendarItem = Calendar.getInstance()
        var counter = 1
        var itemMonthPrevious = 0
        var itemWeekIndex = 0
        val averageAngleList: MutableList<Int> = mutableListOf()
        val distanceList: MutableList<Float> = mutableListOf()
        val caloriesList: MutableList<Float> = mutableListOf()

        for (item in sessionsThisWeek) {
            val itemDate = item.session.endedAt
            val localItemDate =
                itemDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            calendarItem.time = itemDate
            itemWeekIndex = localItemDate.dayOfWeek.value - 1
            Log.d("GraphDataThisWeek","itemWeekIndex: $itemWeekIndex, itemWeek: ${localItemDate.dayOfWeek}")

            if (counter == 1) itemMonthPrevious = itemWeekIndex
            if (itemWeekIndex != itemMonthPrevious) {
                Log.d("GraphDataThisMonth", "Clearing lists")
                counter = 1
                averageAngleList.clear()
                distanceList.clear()
                caloriesList.clear()
            }

            when (selectedVariable) {
                "Distance" -> {
                    val tempDistanceList = mutableListOf<Float>()
                    for (i in item.data) {
                        tempDistanceList.add((i.totalDistance).toFloat() / 1000)
                    }
                    distanceList.add(tempDistanceList.maxOrNull() ?: 0.0f)
                    dayList[itemWeekIndex] = distanceList.sum().toDouble()
                }
                "Avg angle" -> {
                    for (i in item.data) {
                        averageAngleList.add(i.angle)
                    }
                    dayList[itemWeekIndex] = averageAngleList.average()
                }
                "Time" -> {
                    val startTime = item.session.createdAt.time
                    val endTime = item.session.endedAt.time
                    Log.d("GraphDataToday", "startTime: $startTime, endTime: $endTime")
                    val duration = (String.format("%.3f", (((endTime - startTime).toFloat() / 1000) / 60))).toDouble()
                    Log.d("GraphDataToday","duration: $duration minutes")
                    dayList[itemWeekIndex] += duration
                }
                "Calories" -> {
                    val userWeight = database.settingsDao().getBodyWeightById(1)?.weight
                    val tempDistanceList = mutableListOf<Float>()
                    for (i in item.data) {
                        tempDistanceList.add((i.totalDistance).toFloat() / 1000)
                    }
                    val distance = tempDistanceList.maxOrNull() ?: 0.0f
                    val calories = CalorieCounter().countCalories(distance, userWeight ?: 70.0f)
                    caloriesList.add(calories)
                    Log.d("GraphDataToday", "caloriesList: $caloriesList")
                    dayList[itemWeekIndex] = caloriesList.sum().toDouble()
                }
                else -> Log.d("GraphDataThisWeek", "No action set for variable: $selectedVariable")
            }
        }

        // BarGraphSeries is used in generating graph data using DataPoints
        return@withContext BarGraphSeries(
            arrayOf(
                DataPoint(1.0, dayList[0]),
                DataPoint(2.0, dayList[1]),
                DataPoint(3.0, dayList[2]),
                DataPoint(4.0, dayList[3]),
                DataPoint(5.0, dayList[4]),
                DataPoint(6.0, dayList[5]),
                DataPoint(7.0, dayList[6])
            )
        )
    }
}