package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import android.util.Log
import fi.climbstationsolutions.climbstation.ui.viewmodels.InfoPopupViewModel

class MenuActions {
    fun updateUserWeight(context: Context, positiveAction: (userInput: String) -> Unit = {}) {
        return PopupHandlers().editWeightPopup(context, positiveAction)
    }
    fun showInfoPopup(infoRequest: String, context: Context, viewModel: InfoPopupViewModel) {
        if(infoRequest == "How to climb") {
            PopupHandlers().showInfoDialog(infoRequest, context, viewModel)
        }
        if(infoRequest == "How to connect to ClimbStation machine") {
            PopupHandlers().showInfoDialog(infoRequest, context, viewModel)
        }
        if(infoRequest == "How to create custom climbing profiles") {
            PopupHandlers().showInfoDialog(infoRequest, context, viewModel)
        } else {
            Log.d("MenuActions", "No such info popup available")
        }

    }
}