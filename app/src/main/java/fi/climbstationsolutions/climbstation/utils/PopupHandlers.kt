package fi.climbstationsolutions.climbstation.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbFinishedBinding
import fi.climbstationsolutions.climbstation.databinding.InfoPopupBinding
import fi.climbstationsolutions.climbstation.ui.viewmodels.InfoPopupViewModel

class PopupHandlers {
    private lateinit var binding: InfoPopupBinding

    fun showInfoDialog(title: String, context: Context, viewModel: InfoPopupViewModel) {
        binding = InfoPopupBinding.inflate(LayoutInflater.from(context))
        binding.viewModel = viewModel
        Log.d("PopupHandlers", "viewModel: ${viewModel.title.value}")
        viewModel.setTitle(title)
        Log.d("PopupHandlers", "viewModel: ${viewModel.title.value}")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

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

    fun editSpeedPopup(
        context: Context,
        positiveAction: (userInput: Int) -> Unit
    ) {
        // This dialog popup asks the user for their desired climbing speed.
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        // sets a custom dialog interface for the popup
        val li = LayoutInflater.from(context)
        val promptsView = li.inflate(R.layout.speed_prompt, null)
        val speedSpinner = promptsView.findViewById<AppCompatSpinner>(R.id.speed_prompt_spinner)
        builder.setView(promptsView)
        builder.setCancelable(true)

        var selectedSpeed = "Normal"

        val spinnerList: MutableList<String> = ArrayList()
        spinnerList.add("Slow")
        spinnerList.add("Normal")
        spinnerList.add("Fast")
        spinnerList.add("Very fast")

        val customSpinnerAdapter = ArrayAdapter(
            context.applicationContext,
            R.layout.custom_spinner_item,
            spinnerList
        )

        speedSpinner.adapter = customSpinnerAdapter

        speedSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedSpeed = spinnerList[p2]
                }

                // Unused, here to prevent member implementation error
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        // when user clicks "save"
        builder.setPositiveButton("SAVE") { _, _ ->
            var speedNumber = 8
            when (selectedSpeed) {
                "Slow" -> speedNumber = 4
                "Normal" -> speedNumber = 8
                "Fast" -> speedNumber = 12
                "Very fast" -> speedNumber = 16

            }
            positiveAction(speedNumber)
        }

        // when user clicks "cancel"
        builder.setNegativeButton("Cancel") { d, _ ->
            d.cancel()
            Log.d(
                "settings_fragment_speed_popup",
                "prompt canceled"
            )
        }

        // Puts the popup to the screen
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}