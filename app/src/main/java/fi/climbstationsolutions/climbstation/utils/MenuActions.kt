package fi.climbstationsolutions.climbstation.utils

import android.content.Context

class MenuActions {
    fun updateUserWeight(context: Context, positiveAction: (userInput: String) -> Unit = {}) {
        return PopupHandlers().editWeightPopup(context, positiveAction)
    }
}