package fi.climbstationsolutions.climbstation.graph

import android.content.Context
import android.util.Log
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

class GraphDataToday(context: Context) {
    // This holds data to be inserted into DataPoints
    private val hourList: MutableList<Double> = mutableListOf(
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

    suspend fun createGraphData(selectedVariable: String): BarGraphSeries<DataPoint> = withContext(
        Dispatchers.IO
    ) {
        val date = Date()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val year = localDate.year
        val month = localDate.monthValue
        val day = localDate.dayOfMonth

        val beginningOfDay = LocalDateTime.of(year, month, day, 0, 0)
        val daySelected = Date.from(beginningOfDay.toInstant(ZoneOffset.UTC))
        val nextDay = Date.from(beginningOfDay.plusDays(1).toInstant(ZoneOffset.UTC))

        val sessionsToday: List<SessionWithData> =
            sessionDao.getSessionWithDataBetween(daySelected, nextDay)

        val cal = Calendar.getInstance()
        var counter = 1
        var itemHourPrevious = 0
        var itemHour: Int
        val averageAngleList: MutableList<Int> = mutableListOf()
        val distanceList: MutableList<Float> = mutableListOf()
        val caloriesList: MutableList<Float> = mutableListOf()

        // assign items to hourList
        for (item in sessionsToday) {
            val itemDate = item.session.endedAt
            if(itemDate != null) {
                cal.time = itemDate
                itemHour = cal[Calendar.HOUR_OF_DAY]
                if (counter == 1) itemHourPrevious = itemHour
                if (itemHour != itemHourPrevious) {
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
                        hourList[itemHour] = distanceList.sum().toDouble()
                    }
                    "Avg angle" -> {
                        for (i in item.data) {
                            averageAngleList.add(i.angle)
                        }
                        hourList[itemHour] = averageAngleList.average()
                    }
                    "Time" -> {
                        val startTime = item.session.createdAt.time
                        val endTime = item.session.endedAt.time
                        val duration = (String.format(
                            Locale.US,
                            "%.3f",
                            (((endTime - startTime).toFloat() / 1000) / 60)
                        )).toDouble()
                        hourList[itemHour] += duration
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
                        hourList[itemHour] = caloriesList.sum().toDouble()
                    }
                    else -> Log.d("GraphDataToday", "No action set for variable: $selectedVariable")
                }
            }
        }

        // BarGraphSeries is used in generating graph data using DataPoints
        return@withContext BarGraphSeries(
            arrayOf(
                DataPoint(0.0, hourList[0]),
                DataPoint(1.0, hourList[1]),
                DataPoint(2.0, hourList[2]),
                DataPoint(3.0, hourList[3]),
                DataPoint(4.0, hourList[4]),
                DataPoint(5.0, hourList[5]),
                DataPoint(6.0, hourList[6]),
                DataPoint(7.0, hourList[7]),
                DataPoint(8.0, hourList[8]),
                DataPoint(9.0, hourList[9]),
                DataPoint(10.0, hourList[10]),
                DataPoint(11.0, hourList[11]),
                DataPoint(12.0, hourList[12]),
                DataPoint(13.0, hourList[13]),
                DataPoint(14.0, hourList[14]),
                DataPoint(15.0, hourList[15]),
                DataPoint(16.0, hourList[16]),
                DataPoint(17.0, hourList[17]),
                DataPoint(18.0, hourList[18]),
                DataPoint(19.0, hourList[19]),
                DataPoint(20.0, hourList[20]),
                DataPoint(21.0, hourList[21]),
                DataPoint(22.0, hourList[22]),
                DataPoint(23.0, hourList[23]),
            )
        )
    }
}