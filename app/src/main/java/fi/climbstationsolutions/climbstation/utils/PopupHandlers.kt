package fi.climbstationsolutions.climbstation.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.CustomExpandableListAdapter
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*

class PopupHandlers {

    fun editWeightPopup(
        context: Context,
        positiveAction: (userInput: String) -> Unit
    ) {
        // This dialog popup asks the user for their weight in kilograms. It is used in calorie counting
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        // sets a custom dialog interface for the popup
        val li = LayoutInflater.from(context)
        val promptsView = li.inflate(R.layout.weight_prompt, null)
        val userInput = promptsView.findViewById<EditText>(R.id.editTextDialogUserInput)
        builder.setView(promptsView)
        builder.setCancelable(true)

        // when user clicks "save"
        builder.setPositiveButton("SAVE") { _, _ ->
            positiveAction(userInput.text.toString())
        }

        // when user clicks "cancel"
        builder.setNegativeButton("Cancel") { d, _ ->
            d.cancel()
            Log.d(
                "settings_fragment_weight_popup",
                "prompt canceled"
            )
        }

        // Puts the popup to the screen
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}