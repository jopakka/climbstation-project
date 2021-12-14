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

/**
 * @author Oskar Wiiala
 * Generates DataPoints of this year based on variable
 */
class GraphDataThisYear(context: Context) {
    // This holds data to be inserted into DataPoints
    private val monthList: MutableList<Double> = mutableListOf(
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
    )

    private val database = AppDatabase.get(context)
    private val sessionDao = database.sessionDao()

    /**
     * @param [selectedVariable] is the variable which graph data is based on, such as "Distance" or "Calories"
     * @return [BarGraphSeries] contains DataPoints required to generate graph data
     */
    suspend fun createGraphData(selectedVariable: String): BarGraphSeries<DataPoint> =
        withContext(Dispatchers.IO) {
            val date = Date()
            val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val year = localDate.year

            val beginningOfYear = LocalDateTime.of(year, 1, 1, 0, 0)
            val yearSelected = Date.from(beginningOfYear.toInstant(ZoneOffset.UTC))
            val nextYear = Date.from(beginningOfYear.plusYears(1).toInstant(ZoneOffset.UTC))

            val sessionsThisYear: List<SessionWithData> =
                sessionDao.getSessionWithDataBetween(yearSelected, nextYear)

            val cal = Calendar.getInstance()
            var counter = 1
            var itemMonthPrevious = 0
            var itemMonthIndex: Int
            val averageAngleList: MutableList<Int> = mutableListOf()
            val distanceList: MutableList<Float> = mutableListOf()
            val caloriesList: MutableList<Float> = mutableListOf()

            for (item in sessionsThisYear) {
                val itemDate = item.session.endedAt
                if (itemDate != null) {
                    val localItemDate =
                        itemDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    cal.time = itemDate
                    itemMonthIndex = localItemDate.monthValue - 1

                    if (counter == 1) itemMonthPrevious = itemMonthIndex
                    if (itemMonthIndex != itemMonthPrevious) {
                        counter = 1
                        averageAngleList.clear()
                        distanceList.clear()
                        caloriesList.clear()
                        caloriesList.clear()
                    }

                    when (selectedVariable) {
                        "Distance" -> {
                            val tempDistanceList = mutableListOf<Float>()
                            for (i in item.data) {
                                tempDistanceList.add((i.totalDistance).toFloat() / 1000)
                            }
                            distanceList.add(tempDistanceList.maxOrNull() ?: 0.0f)
                            monthList[itemMonthIndex] = distanceList.sum().toDouble()
                        }
                        "Avg angle" -> {
                            for (i in item.data) {
                                averageAngleList.add(i.angle)
                            }
                            monthList[itemMonthIndex] = averageAngleList.average()
                        }
                        "Time" -> {
                            val startTime = item.session.createdAt.time
                            val endTime = item.session.endedAt.time
                            val duration = (String.format(
                                Locale.US,
                                "%.3f",
                                (((endTime - startTime).toFloat() / 1000) / 60)
                            )).toDouble()
                            monthList[itemMonthIndex] += duration
                        }
                        "Calories" -> {
                            val userWeight = database.settingsDao().getBodyWeightById(1)?.weight
                            val tempDistanceList = mutableListOf<Float>()
                            for (i in item.data) {
                                tempDistanceList.add((i.totalDistance).toFloat() / 1000)
                            }
                            val distance = tempDistanceList.maxOrNull() ?: 0.0f
                            val calories =
                                CalorieCounter().countCalories(distance, userWeight ?: 70.0f)
                            caloriesList.add(calories)
                            monthList[itemMonthIndex] = caloriesList.sum().toDouble()
                        }
                        else -> Log.d(
                            "GraphDataThisYear",
                            "No action set for variable: $selectedVariable"
                        )
                    }
                }
            }

            // LineGraphSeries is used in generating graph data using DataPoints
            return@withContext BarGraphSeries(
                arrayOf(
                    DataPoint(1.0, monthList[0]),
                    DataPoint(2.0, monthList[1]),
                    DataPoint(3.0, monthList[2]),
                    DataPoint(4.0, monthList[3]),
                    DataPoint(5.0, monthList[4]),
                    DataPoint(6.0, monthList[5]),
                    DataPoint(7.0, monthList[6]),
                    DataPoint(8.0, monthList[7]),
                    DataPoint(9.0, monthList[8]),
                    DataPoint(10.0, monthList[9]),
                    DataPoint(11.0, monthList[10]),
                    DataPoint(12.0, monthList[11]),
                )
            )
        }
}