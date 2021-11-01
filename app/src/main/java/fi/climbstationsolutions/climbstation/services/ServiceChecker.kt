package fi.climbstationsolutions.climbstation.services

import android.app.ActivityManager
import android.content.Context

object ServiceChecker {
    // https://stackoverflow.com/a/5921190
    @Suppress("DEPRECATION")
    fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}