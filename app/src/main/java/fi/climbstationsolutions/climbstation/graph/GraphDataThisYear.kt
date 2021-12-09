package fi.climbstationsolutions.climbstation.graph

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphDataThisYear(context: Application) {
    // This holds data to be inserted into DataPoints
    private val monthList: MutableList<Double> = mutableListOf(
        40.0,
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createGraphData(selectedVariable: String): BarGraphSeries<DataPoint> = withContext(Dispatchers.IO) {

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