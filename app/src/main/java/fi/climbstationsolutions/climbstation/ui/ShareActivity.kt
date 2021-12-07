package fi.climbstationsolutions.climbstation.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.utils.ProfileSharer

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        initUI()
    }

    private fun initUI() {
        val prof = readProfile()
        if (prof == null) {
            showErrorDialog()
        } else {
            listProfileSteps(prof)
        }
    }

    private fun listProfileSteps(profileWithSteps: ClimbProfileWithSteps) {

    }

    private fun readProfile(): ClimbProfileWithSteps? {
        val path = intent?.data ?: return null
        val sharer = ProfileSharer(this)
        return sharer.getSharedProfile(path)
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
}