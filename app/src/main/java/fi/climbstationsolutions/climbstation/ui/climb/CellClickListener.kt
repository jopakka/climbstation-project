package fi.climbstationsolutions.climbstation.ui.climb

import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps

interface CellClickListener {
    fun onCellClickListener(profile: ClimbProfileWithSteps)
}