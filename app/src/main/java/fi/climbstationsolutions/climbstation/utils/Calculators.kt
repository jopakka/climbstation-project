package fi.climbstationsolutions.climbstation.utils

import fi.climbstationsolutions.climbstation.database.ClimbStep

object Calculators {
    fun averageAngleFromSteps(steps: List<ClimbStep>): Float {
        if (steps.isEmpty()) return 0f

        var amountOfDistances = 0
        val angles = steps.sumOf {
            amountOfDistances += it.distance
            it.distance * it.angle
        }

        return angles.toFloat() / amountOfDistances.toFloat()
    }
}