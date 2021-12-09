package fi.climbstationsolutions.climbstation.graph

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphDataToday(context: Application) {
    // This holds data to be inserted into DataPoints
    private val hourList: MutableList<Double> = mutableListOf(
        10.0,
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createGraphData(selectedVariable: String): BarGraphSeries<DataPoint> = withContext(
        Dispatchers.IO
    ) {
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