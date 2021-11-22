package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModel

class MenuActions {
    suspend fun updateUserWeight(context: Context, activity: Context, viewModel: MainActivityViewModel): Boolean {
        return PopupHandlers().editWeightPopup(context, activity, viewModel)
    }
}