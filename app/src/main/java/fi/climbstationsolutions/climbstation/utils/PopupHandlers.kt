package fi.climbstationsolutions.climbstation.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*

class PopupHandlers: AppCompatActivity() {
    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private var result = false
    private var looper = true
    private lateinit var test: Deferred<Boolean>
    private lateinit var job: Job

    override fun onPause() {
        super.onPause()
        looper = false
    }

    override fun onStop() {
        super.onStop()
        looper = false
    }

    override fun onDestroy() {
        super.onDestroy()
        looper = false
    }

    suspend fun editWeightPopup(
        context: Context,
        activity: Context,
        viewModel: MainActivityViewModel
    ): Boolean = withContext(Dispatchers.Main) {
        // This dialog popup asks the user for their weight in kilograms. It is used in calorie counting
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        // sets a custom dialog interface for the popup
        val li = LayoutInflater.from(context.applicationContext)
        val promptsView = li.inflate(R.layout.weight_prompt, null)
        builder.setView(promptsView)
        // InputField to set user's weight
        val userInput = promptsView.findViewById<EditText>(R.id.editTextDialogUserInput)
        builder.setCancelable(true)

        // when user clicks "save"
        job = launch(Dispatchers.IO) {
            builder.setPositiveButton("SAVE") { _, _ ->
                val settingsDao = AppDatabase.get(context).settingsDao()
                val userInputFloatOrNull = userInput.text.toString().toFloatOrNull()
                if (userInput.text.isNotEmpty() && userInputFloatOrNull != null) {
                    val userWeightKg = userInput.text.toString().toFloat()

                    ioScope.launch {
                        test = async {
                            val userWeight = settingsDao.getBodyWeightById(1)
                            Log.d("MenuViewModel", "userWeight: $userWeight")
                            if (userWeight != null) {
                                settingsDao.updateUserBodyWeight(userWeightKg)
                                return@async true
                            } else {
                                return@async false
                            }
                        }
                        result = test.await()
                        looper = false
                    }

                } else {
                    test = async { return@async false }
                    looper = false
                    Log.d(
                        "settings_fragment_weight_popup",
                        "user weight is in incorrect format or is empty"
                    )
                }
            }
            // waits for user input to finish before continuing
            // Not ideal but unfortunately I was unable to find any other way of doing this.
            while (looper) {
//                Log.d("loop","loop")
            }
            Log.d("menuChildClick", "exiting job")
        }

        // when user clicks "cancel"
        builder.setNegativeButton("Cancel") { _, _ ->
            test = async { return@async false }
            looper = false
            Log.d(
                "settings_fragment_weight_popup",
                "prompt canceled"
            )
        }

        builder.setOnCancelListener {
            test = async { return@async false }
            looper = false
        }

        builder.setOnDismissListener {
            test = async { return@async false }
            looper = false
        }

        // Puts the popup to the screen
        val dialog: AlertDialog = builder.create()
        dialog.show()
        job.join()
        return@withContext test.await()
    }
}