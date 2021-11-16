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
}