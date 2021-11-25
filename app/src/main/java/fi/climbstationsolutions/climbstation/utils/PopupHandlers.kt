package fi.climbstationsolutions.climbstation.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding
import fi.climbstationsolutions.climbstation.databinding.InfoPopupBinding
import fi.climbstationsolutions.climbstation.ui.viewmodels.InfoPopupViewModel

class PopupHandlers {
    private lateinit var binding: InfoPopupBinding

    fun showInfoDialog(title: String, context: Context, viewModel: InfoPopupViewModel) {
//        context.let {
//            val builder = AlertDialog.Builder(it).apply {
//                setTitle(R.string.error)
//                setMessage("this is a message")
//                setPositiveButton(android.R.string.ok) { d, _ ->
//                    d.cancel()
//                }
//            }
//            val dialog = builder.create()
//            dialog.show()
//        }

//        if (title == "How to climb") {
//            viewModel.setTitle(title)
//        }
//        if (title == "How to connect to ClimbStation machine") {
//
//        } else {
//            Log.d("PopupHandlers", "No dialog for $title")
        binding = InfoPopupBinding.inflate(LayoutInflater.from(context))
//        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
//        }
        Log.d("PopupHandlers","viewModel: ${viewModel.title.value}")
        viewModel.setTitle(title)
        Log.d("PopupHandlers","viewModel: ${viewModel.title.value}")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val li = LayoutInflater.from(context)
        builder.setView(binding.root)
        builder.setPositiveButton(android.R.string.ok) { d, _ ->
            d.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

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