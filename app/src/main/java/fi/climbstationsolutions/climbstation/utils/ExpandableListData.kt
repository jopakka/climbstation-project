package fi.climbstationsolutions.climbstation.utils

import java.util.*

internal object ExpandableListData {
    val data: HashMap<String, List<String>>
        get() {
            val expandableListDetail =
                HashMap<String, List<String>>()
            val settingsItems: MutableList<String> =
                ArrayList()
            val infoItems: MutableList<String> = ArrayList()

            settingsItems.add("Bodyweight")
            expandableListDetail["Settings"] = settingsItems

            infoItems.add("How to connect to ClimbStation machine")
            infoItems.add("How to climb")
            infoItems.add("How to create custom climbing profiles")
            expandableListDetail["Info"] = infoItems

            return expandableListDetail
        }
}
