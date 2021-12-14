package fi.climbstationsolutions.climbstation.ui.climb.climbSetup

import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps

interface CellClickListener {
    fun onCellClickListener(profile: ClimbProfileWithSteps)
}