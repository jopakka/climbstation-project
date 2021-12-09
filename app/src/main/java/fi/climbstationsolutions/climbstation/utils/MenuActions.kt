package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import android.util.Log
import fi.climbstationsolutions.climbstation.ui.viewmodels.InfoPopupViewModel

class MenuActions {
    fun updateUserWeight(context: Context, positiveAction: (userInput: String) -> Unit = {}) {
        return PopupHandlers().editWeightPopup(context, positiveAction)
    }
    fun showInfoPopup(infoRequest: String, context: Context, viewModel: InfoPopupViewModel) {
        val handler = PopupHandlers()
        when(infoRequest) {
            "How to climb",
            "How to connect to ClimbStation machine",
            "How to create custom climbing profiles",
            "Developers" -> handler.showInfoDialog(infoRequest, context, viewModel)
            else -> Log.d("MenuActions", "No such info popup available")
        }
    }
}