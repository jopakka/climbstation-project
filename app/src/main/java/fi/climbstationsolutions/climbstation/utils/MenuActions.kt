package fi.climbstationsolutions.climbstation.utils

import android.content.Context
import android.util.Log
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.set
import fi.climbstationsolutions.climbstation.sharedprefs.TTS_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.InfoPopupViewModel

/**
 * @author Oskar Wiiala
 * Handles most actions in the top app bar side menu
 */
class MenuActions {
    fun updateUserWeight(context: Context, positiveAction: (userInput: String) -> Unit = {}) {
        return MenuPopupHandlers().editWeightPopup(context, positiveAction)
    }

    fun showInfoPopup(infoRequest: String, context: Context, viewModel: InfoPopupViewModel) {
        val handler = MenuPopupHandlers()
        when (infoRequest) {
            "How to climb",
            "How to connect to ClimbStation machine",
            "How to create custom climbing profiles",
            "Developers" -> handler.showInfoDialog(infoRequest, context, viewModel)
            else -> Log.d("MenuActions", "No such info popup available: $infoRequest")
        }
    }

    fun updateSpeed(context: Context, positiveAction: (userInput: Int) -> Unit = {}) {
        return MenuPopupHandlers().editSpeedPopup(context, positiveAction)
    }

    // Tts = text to speech
    fun toggleTts(context: Context) {
        val prefs = PreferenceHelper.customPrefs(context, PREF_NAME)
        val isTtsOn: Boolean = prefs[TTS_PREF_NAME]
        if (isTtsOn) {
            Log.d("toggleTts", "isOn")
            prefs[TTS_PREF_NAME] = false
        } else if (!isTtsOn) {
            Log.d("toggleTts", "isNotOn")
            prefs[TTS_PREF_NAME] = true

        }
    }
}