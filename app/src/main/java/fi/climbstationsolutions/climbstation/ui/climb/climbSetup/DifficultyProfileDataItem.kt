package fi.climbstationsolutions.climbstation.ui.climb.climbSetup

import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps

sealed class DifficultyProfileDataItem {
    abstract val id: Long

    data class DifficultyItem(val climbProfileWithSteps: ClimbProfileWithSteps) : DifficultyProfileDataItem() {
        override val id = climbProfileWithSteps.profile.id
    }

    object DifficultyHeader1Item : DifficultyProfileDataItem() {
        override val id = Long.MIN_VALUE
    }

    data class DifficultyHeader2Item(val default: Boolean) : DifficultyProfileDataItem() {
        override val id = Long.MIN_VALUE
    }
}
