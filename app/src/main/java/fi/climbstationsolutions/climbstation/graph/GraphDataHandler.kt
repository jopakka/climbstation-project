package fi.climbstationsolutions.climbstation.graph

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint

class GraphDataHandler(application: Application): AndroidViewModel(application) {
    private val appContext = application
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGraphDataPointsOfToday(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataToday(appContext).createGraphData(selectedVariable)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGraphDataPointsOfThisWeek(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataThisWeek(appContext).createGraphData(selectedVariable)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGraphDataPointsOfThisMonth(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataThisMonth(appContext).createGraphData(selectedVariable)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGraphDataPointsOfThisYear(selectedVariable: String): BarGraphSeries<DataPoint> {
        return GraphDataThisYear(appContext).createGraphData(selectedVariable)
    }
}