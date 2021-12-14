package fi.climbstationsolutions.climbstation.utils

class CalorieCounter {
    fun countCalories(
        totalDistance: Float,
        userWeight: Float
    ): Float {
        val force = userWeight * 9.8
        val energy = force * totalDistance
        return String.format(null,"%.6f", energy * 0.24 / 1000 / 2.25).toFloat()
    }
}