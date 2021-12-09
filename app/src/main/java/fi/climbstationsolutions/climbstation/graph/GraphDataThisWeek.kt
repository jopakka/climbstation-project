package fi.climbstationsolutions.climbstation.graph

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphDataThisWeek(context: Application) {
    // This holds data to be inserted into DataPoints
    private val dayList: MutableList<Double> = mutableListOf(20.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createGraphData(selectedVariable: String): BarGraphSeries<DataPoint> = withContext(
        Dispatchers.IO
    ) {
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