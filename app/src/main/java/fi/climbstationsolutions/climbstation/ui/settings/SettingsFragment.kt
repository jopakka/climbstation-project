package fi.climbstationsolutions.climbstation.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SettingsDao
import fi.climbstationsolutions.climbstation.databinding.FragmentSettingsBinding
import fi.climbstationsolutions.climbstation.ui.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var userBodyWeightDefault: Float = 70.00F

    private lateinit var settingsDao: SettingsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDao = AppDatabase.get(requireContext()).settingsDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeValues()
    }

    private fun initializeValues() {
        ioScope.launch {
            val user = settingsDao.getBodyWeightById(1)
            if (user == null) {
                viewModel.setUserWeight(userBodyWeightDefault)
            } else {
                mainScope.launch { viewModel.setUserWeight(user.weight) }
            }
        }
        binding.settingsBtnEditBodyWeight.setOnClickListener(clickListener)
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
                    val newUserBodyWeight = settingsDao.getBodyWeightById(1)?.weight
                    if (newUserBodyWeight != null) {
                        mainScope.launch { viewModel.setUserWeight(newUserBodyWeight) }
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

    private val clickListener = View.OnClickListener {
        when (it) {
            binding.settingsBtnEditBodyWeight -> {
                editWeightPopup()
            }
        }
    }
}