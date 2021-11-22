package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModel

class MenuActions {
    fun updateUserWeight(context: Context, positiveAction: (userInput: String) -> Unit = {}) {
        return PopupHandlers().editWeightPopup(context, positiveAction)
    }
}