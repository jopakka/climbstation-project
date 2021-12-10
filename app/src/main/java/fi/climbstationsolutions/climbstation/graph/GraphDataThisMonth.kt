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

class GraphDataThisMonth(context: Context) {
    // This holds data to be inserted into DataPoints
    private val dayList: MutableList<Double> = mutableListOf(
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0
    )

    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createGraphData(selectedVariable: String): BarGraphSeries<DataPoint> = withContext(
        Dispatchers.IO
    ) {

        val date = Date()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val year = localDate.year
        val month = localDate.monthValue
        val day = localDate.dayOfMonth
        val currentYearMonthDay = year.toString() + month.toString() + day.toString()

        val beginningOfMonth = LocalDateTime.of(year, month, 1, 0, 0)
        val monthSelected = Date.from(beginningOfMonth.toInstant(ZoneOffset.UTC))
        val nextMonth = Date.from(beginningOfMonth.plusMonths(1).toInstant(ZoneOffset.UTC))

        val sessionsThisMonth: List<SessionWithData> =
            sessionDao.getSessionWithDataBetween(monthSelected, nextMonth)
        Log.d("graphDataThisMonth", "sessionsThisMonth: $sessionsThisMonth")

        val cal = Calendar.getInstance()
        var counter = 1
        var itemDayPrevious = 0
        var itemDayIndex = 0
        val averageAngleList: MutableList<Int> = mutableListOf()
        val distanceList: MutableList<Float> = mutableListOf()
        val caloriesList: MutableList<Float> = mutableListOf()

        // assign items to hourList
        for (item in sessionsThisMonth) {
//            Log.d("GraphDataThisMonth", "item: ${item}")
            val itemDate = item.session.endedAt
            val localItemDate = itemDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            cal.time = itemDate!!
//            itemDay = cal[Calendar.DAY_OF_MONTH]
            itemDayIndex = localItemDate.dayOfMonth - 1
            Log.d("GraphDataThisMonth", "itemDay: $itemDayIndex, itemDayPrevious: $itemDayPrevious")
            Log.d("GraphDataThisMonth", "Clearing lists1")
            if (counter == 1) itemDayPrevious = itemDayIndex
            if (itemDayIndex != itemDayPrevious) {
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
                    dayList[itemDayIndex] = distanceList.sum().toDouble()
                }
                "Avg angle" -> {
                    for (i in item.data) {
                        averageAngleList.add(i.angle)
                    }
                    dayList[itemDayIndex] = averageAngleList.average()
                }
                "Time" -> {
                    val startTime = item.session.createdAt.time
                    val endTime = item.session.endedAt.time
                    Log.d("GraphDataToday", "startTime: $startTime, endTime: $endTime")
                    val duration = (String.format(
                        "%.3f",
                        (((endTime - startTime).toFloat() / 1000) / 60)
                    )).toDouble()
                    Log.d("GraphDataToday", "duration: $duration minutes")
                    dayList[itemDayIndex] += duration
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
                    dayList[itemDayIndex] = caloriesList.sum().toDouble()
                }
                else -> Log.d("GraphDataThisMonth","No action set for variable: $selectedVariable")
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
                DataPoint(7.0, dayList[6]),
                DataPoint(8.0, dayList[7]),
                DataPoint(9.0, dayList[8]),
                DataPoint(10.0, dayList[9]),
                DataPoint(11.0, dayList[10]),
                DataPoint(12.0, dayList[11]),
                DataPoint(13.0, dayList[12]),
                DataPoint(14.0, dayList[13]),
                DataPoint(15.0, dayList[14]),
                DataPoint(16.0, dayList[15]),
                DataPoint(17.0, dayList[16]),
                DataPoint(18.0, dayList[17]),
                DataPoint(19.0, dayList[18]),
                DataPoint(20.0, dayList[19]),
                DataPoint(21.0, dayList[20]),
                DataPoint(22.0, dayList[21]),
                DataPoint(23.0, dayList[22]),
                DataPoint(24.0, dayList[23]),
                DataPoint(25.0, dayList[24]),
                DataPoint(26.0, dayList[25]),
                DataPoint(27.0, dayList[26]),
                DataPoint(28.0, dayList[27]),
                DataPoint(29.0, dayList[28]),
                DataPoint(30.0, dayList[29]),
                DataPoint(31.0, dayList[30]),
            )
        )
    }
}