package fi.climbstationsolutions.climbstation.ui.init

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class GetSerial : ActivityResultContract<Unit, String?>() {
    override fun createIntent(context: Context, input: Unit?) =
        Intent(context, InitActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return intent?.getStringExtra(SerialFragment.EXTRA_SERIAL)
    }

}