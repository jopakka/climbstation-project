package fi.climbstationsolutions.climbstation.ui.share

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.ActivityShareBinding
import fi.climbstationsolutions.climbstation.utils.ProfileSharer

class ShareActivity : AppCompatActivity() {
    private val viewModel: ShareViewModel by viewModels()
    private lateinit var binding: ActivityShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        val prof = readProfile()
        if (prof == null) {
            showErrorDialog()
        } else {
            initRecyclerView()
            initMenuClickListener()
            listProfileSteps(prof)
        }
    }

    private fun initRecyclerView() {
        binding.customStepsRv.apply {
            layoutManager = LinearLayoutManager(this@ShareActivity)
            adapter = CustomStepsViewAdapter()
        }
    }

    private fun initMenuClickListener() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuSave -> {
                    addNamePopUp()
                    true
                }
                else -> false
            }
        }
    }

    private fun addLoadingListener() {
        viewModel.loading.observe(this) {
            if (it == false) {
                showSaveDoneDialog()
            }
        }
    }

    private fun listProfileSteps(profileWithSteps: ClimbProfileWithSteps) {
        viewModel.setProfileWithSteps(profileWithSteps)
        val adapter = binding.customStepsRv.adapter as CustomStepsViewAdapter
        viewModel.profileWithSteps.observe(this) {
            it ?: return@observe
            adapter.addProfiles(it.steps)
        }
    }

    private fun readProfile(): ClimbProfileWithSteps? {
        val path = intent?.data ?: return null
        val sharer = ProfileSharer(this)
        return sharer.getSharedProfile(path)
    }

    private fun addNamePopUp() {
        val builder = AlertDialog.Builder(this)
        val promptsView = View.inflate(this, R.layout.custom_profile_prompt, null)

        val userInput = promptsView.findViewById<EditText>(R.id.custom_profile_dialog_editText)
        userInput.setText(viewModel.profileWithSteps.value?.profile?.name ?: "")

        builder.apply {
            setView(promptsView)
            setPositiveButton(R.string.add) { _, _ ->
                if (userInput.text.toString() != "") {
                    addLoadingListener()
                    viewModel.saveProfile(userInput.text.toString())
                }
            }
            setNegativeButton(R.string.cancel) { _, _ ->
                Log.d("Cancel", "WORKS")
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.not_valid_profile)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showSaveDoneDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.saved)
            .setMessage(R.string.profile_saved_successfully)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
        val dialog = builder.create()
        dialog.show()
    }
}