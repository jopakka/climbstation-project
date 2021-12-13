package fi.climbstationsolutions.climbstation

import fi.climbstationsolutions.climbstation.utils.CalorieCounter
import fi.climbstationsolutions.climbstation.utils.Converters
import org.junit.Assert
import org.junit.Test
import java.util.*

class UtilTests {

    @Test
    fun calorieCounter() {
        Assert.assertEquals(1.71672f, CalorieCounter().countCalories(20.3f, 80.9f))
    }

    @Test
    fun fromTimestamp() {
        val date = Date(1636450470248L)
//        "Tue Nov 09 11:34:30 GMT+02:00 2021"
        Assert.assertEquals(date, Converters.fromTimestamp(1636450470248L))
    }

    @Test
    fun dateToTimestamp() {
        val date = Date(1636450470248L)
        Assert.assertEquals(1636450470248L, Converters.dateToTimestamp(date))
    }
}