package fi.climbstationsolutions.climbstation.utils

import fi.climbstationsolutions.climbstation.database.ClimbStep
import kotlin.math.cos

/**
 * @author Joonas Niemi
 */
object Calculators {
    /**
     * Calculates average angle between [steps]
     */
    fun averageAngleFromSteps(steps: List<ClimbStep>): Float {
        if (steps.isEmpty()) return 0f

        var amountOfDistances = 0
        val angles = steps.sumOf {
            amountOfDistances += it.distance
            it.distance * it.angle
        }

        return angles.toFloat() / amountOfDistances.toFloat()
    }

    /**
     * Calculates [steps] total distance
     */
    fun calculateDistance(steps: List<ClimbStep>): Int {
        return steps.sumOf { it.distance }
    }

    /**
     * Calculates vertical height of [steps]
     */
    fun calculateHeight(steps: List<ClimbStep>): Float {
        var height = 0f
        steps.forEach { s ->
            val h = (cos(Math.toRadians(s.angle.toDouble())) * s.distance).toFloat()
            height += h
        }
        return height
    }
}