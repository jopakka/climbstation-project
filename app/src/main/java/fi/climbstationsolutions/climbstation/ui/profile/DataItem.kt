package fi.climbstationsolutions.climbstation.ui.profile

import fi.climbstationsolutions.climbstation.database.SessionWithData
import java.time.YearMonth

sealed class DataItem {
    abstract val id: Long

    data class SessionItem(val data: SessionWithData) : DataItem() {
        override val id = data.session.id
    }

    data class HeaderItem(val yearMonth: YearMonth?, var sessionCount: Int = 0) : DataItem() {
        override val id = Long.MIN_VALUE
    }

    object EmptyListItem : DataItem() {
        override val id = Long.MIN_VALUE
    }
}

