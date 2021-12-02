package fi.climbstationsolutions.climbstation.ui.climb

import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps

sealed class DifficultyProfileDataItem {
    abstract val id: Long

    data class DifficultyItem(val climbProfileWithSteps: ClimbProfileWithSteps) : DifficultyProfileDataItem() {
        override val id = climbProfileWithSteps.profile.id
    }

    object DifficultyHeaderItem : DifficultyProfileDataItem() {
        override val id = Long.MIN_VALUE
    }
}
