package fi.climbstationsolutions.climbstation.utils

/**
 * @author Oskar Wiiala
 */
class CalorieCounter {

    /**
     * counts calories for climbing session
     * @param [totalDistance] distance in metres
     * @param [userWeight] weight of user in kilograms
     * Not very accurate but it's something
     */
    fun countCalories(
        totalDistance: Float,
        userWeight: Float
    ): Float {
        val force = userWeight * 9.8
        val energy = force * totalDistance
        return String.format(null, "%.6f", energy * 0.24 / 1000 / 2.25).toFloat()
    }
}