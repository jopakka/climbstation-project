package fi.climbstationsolutions.climbstation.graph

import android.content.Context
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint

class GraphDataHandler(context: Context) {
    private val appContext = context

    suspend fun getGraphDataPointsOfToday(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataToday(appContext).createGraphData(selectedVariable)
    }

    suspend fun getGraphDataPointsOfThisWeek(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataThisWeek(appContext).createGraphData(selectedVariable)
    }

    suspend fun getGraphDataPointsOfThisMonth(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataThisMonth(appContext).createGraphData(selectedVariable)
    }

    suspend fun getGraphDataPointsOfThisYear(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataThisYear(appContext).createGraphData(selectedVariable)
    }
}