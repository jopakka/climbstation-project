package fi.climbstationsolutions.climbstation.ui.climb

import fi.climbstationsolutions.climbstation.network.profile.Profile

interface CellClicklistener {
    fun onCellClickListener(profile: Profile)
}