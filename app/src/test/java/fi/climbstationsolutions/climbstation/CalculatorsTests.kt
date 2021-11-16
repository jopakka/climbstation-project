package fi.climbstationsolutions.climbstation

import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.utils.Calculators
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorsTests {
    @Test
    fun angleEmptyList() {
        val steps = listOf<ClimbStep>()
        val average = Calculators.averageAngleFromSteps(steps)
        assertEquals(0f, average)
    }

    @Test
    fun angleRealList() {
        val steps = listOf(
            ClimbStep(1, 1, 10, 45),
            ClimbStep(1, 1, 2, -5),
            ClimbStep(1, 1, 4, 0),
        )
        val average = Calculators.averageAngleFromSteps(steps)
        assertEquals(27.5f, average)
    }

    @Test
    fun distanceEmptyList() {
        val steps = listOf<ClimbStep>()
        val distance = Calculators.calculateDistance(steps)
        assertEquals(0, distance)
    }

    @Test
    fun distanceRealList() {
        val steps = listOf(
            ClimbStep(1, 3, 12, 45),
            ClimbStep(1, 3, 3, -5),
            ClimbStep(1, 3, 5, 0),
        )
        val distance = Calculators.calculateDistance(steps)
        assertEquals(20, distance)
    }

    @Test
    fun heightEmptyList() {
        val steps = listOf<ClimbStep>()
        val height = Calculators.calculateHeight(steps)
        assertEquals(0f, height)
    }

    @Test
    fun heightRealList() {
        val steps = listOf(
            ClimbStep(1, 3, 2, 34),
            ClimbStep(1, 3, 4, -5),
            ClimbStep(1, 3, 6, -45),
        )
        val distance = Calculators.calculateHeight(steps)
        assertEquals(9.885494f, distance)
    }
}