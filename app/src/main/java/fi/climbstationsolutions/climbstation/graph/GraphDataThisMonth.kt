package fi.climbstationsolutions.climbstation.graph

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphDataThisMonth(context: Application) {
    // This holds data to be inserted into DataPoints
    private val dayList: MutableList<Double> = mutableListOf(
        30.0,
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