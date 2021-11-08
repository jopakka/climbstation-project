package fi.climbstationsolutions.climbstation.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.replace
import com.google.android.material.button.MaterialButton
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.BodyWeight
import fi.climbstationsolutions.climbstation.database.SettingsDao
import fi.climbstationsolutions.climbstation.ui.climb.ClimbFinishedFragment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var userBodyWeightDefault: Float = 70.00F

    private lateinit var settingsDao: SettingsDao

    private lateinit var settingsBodyWeight: TextView
    private lateinit var editWeightBtn: MaterialButton
    private lateinit var toClimbFinishedBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDao = AppDatabase.get(requireContext()).settingsDao()
        ioScope.launch {
            val userWeight = settingsDao.getBodyWeightById(1)
            // not sure why the IDE is warning about this, it is sometimes true
            if (userWeight == null) {
                settingsDao.insertUserBodyWeight(BodyWeight(1, userBodyWeightDefault))
            } else {
                Log.d(
                    "settings_fragment_oncreate",
                    "user weight already exists: ${settingsDao.getBodyWeightById(1)}"
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)
    }

    private fun initializeValues(view: View) {
        settingsBodyWeight = view.findViewById(R.id.settings_body_weight)
        editWeightBtn = view.findViewById(R.id.settings_btn_edit_body_weight)
        toClimbFinishedBtn = view.findViewById(R.id.toClimbFinished)

        ioScope.launch {
            val userWeight = settingsDao.getBodyWeightById(1)
            Log.d("settings_fragment_initvalues", "userWeight: $userWeight")
            if (userWeight == null) {
                mainScope.launch {
                    settingsBodyWeight.text =
                        getString(R.string.fragment_settings_weight, userBodyWeightDefault)
                }
            } else {
                mainScope.launch {
                    settingsBodyWeight.text =
                        getString(R.string.fragment_settings_weight, userWeight.weight)
                }
            }
        }

        editWeightBtn.setOnClickListener {
            editWeightPopup()
        }

        toClimbFinishedBtn.setOnClickListener {
            navigateToClimbFinished()
        }
    }

    private fun editWeightPopup() {
        // This dialog popup asks the user for their weight in kilograms. It is used in calorie counting
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        // sets a custom dialog interface for the popup
        val li = LayoutInflater.from(activity?.applicationContext)
        val promptsView = li.inflate(R.layout.weight_prompt, null)
        builder.setView(promptsView)
        // InputField to set user's weight
        val userInput = promptsView.findViewById<EditText>(R.id.editTextDialogUserInput)
        builder.setCancelable(true)

        // when user clicks "save"
        builder.setPositiveButton("SAVE") { _, _ ->
            val userInputFloatOrNull = userInput.text.toString().toFloatOrNull()
            if (userInput.text.isNotEmpty() && userInputFloatOrNull != null) {
                val userWeightKg = userInput.text.toString().toFloat()
                ioScope.launch {
                    settingsDao.updateUserBodyWeight(userWeightKg)
                    val newUserBodyWeight = settingsDao.getBodyWeightById(1).weight
                    mainScope.launch {
                        settingsBodyWeight.text =
                            getString(R.string.fragment_settings_weight, newUserBodyWeight)
                    }
                }
            } else {
                Log.d(
                    "settings_fragment_weight_popup",
                    "user weight is in incorrect format or is empty"
                )

            }
        }

        // when user clicks "cancel"
        builder.setNegativeButton("Cancel") { _, _ ->
            Log.d(
                "settings_fragment_weight_popup",
                "prompt canceled"
            )
        }

        // Puts the popup to the screen
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun navigateToClimbFinished() {
        val sfm = requireActivity().supportFragmentManager
        Log.d("MainActivity.kt","BottomNavigation tracker clicked")
        val transaction = sfm.beginTransaction()
        transaction.replace<ClimbFinishedFragment>(R.id.fragmentContainer)
        transaction.commit()
    }
}