package fi.climbstationsolutions.climbstation.ui.climb

data class DifficultyProfile(
    val climbDif: String,
    val climbDistance: Int,
    val climbSpeed: Int,
    val climbAngle: Int
)

object GlobalModel {
    val ADJUSTMENTS: MutableList<DifficultyProfile> = mutableListOf()

    init {
        ADJUSTMENTS.add(DifficultyProfile("Beginner", 10, 5, 0))
        ADJUSTMENTS.add(DifficultyProfile("Warm up", 14, 8, 4))
        ADJUSTMENTS.add(DifficultyProfile("Easy", 18, 11, 8))
        ADJUSTMENTS.add(DifficultyProfile("Endurance", 22, 14, 10))
        ADJUSTMENTS.add(DifficultyProfile("Strength", 26, 17, 14))
        ADJUSTMENTS.add(DifficultyProfile("Power", 30, 20, 18))
        ADJUSTMENTS.add(DifficultyProfile("Athlete", 34, 22, 20))
        ADJUSTMENTS.add(DifficultyProfile("Pro Athlete", 38, 24, 25))
        ADJUSTMENTS.add(DifficultyProfile("Superhuman", 42, 26, 30))
        ADJUSTMENTS.add(DifficultyProfile("Conqueror", 46, 28, 45))
    }
}