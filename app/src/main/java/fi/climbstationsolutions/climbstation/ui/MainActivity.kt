package fi.climbstationsolutions.climbstation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.climb.ClimbFragment
import fi.climbstationsolutions.climbstation.ui.init.InitActivity
import fi.climbstationsolutions.climbstation.ui.settings.SettingsFragment
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ClimbStation)
        super.onCreate(savedInstanceState)

        // Un-comment this if you want to connect to server
//        startActivity(Intent(this, ClimbActionActivity::class.java))
//        finish()

        /*
            If no serialNo found, then start InitActivity
            else continue with MainActivity
         */
        if(!hasSerialNo()){
            val initIntent = Intent(this, InitActivity::class.java)
            startActivity(initIntent)
            finish()
        } else {
            setContentView(R.layout.activity_main)

            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.climb -> {
                        // Respond to navigation item 1 click
                        navigateToClimb()
                        true
                    }
                    R.id.statistics -> {
                        // Respond to navigation item 2 click
                        navigateToStatistics()
                        true
                    }
                    R.id.settings -> {
                        // Respond to navigation item 3 click
                        navigateToSettings()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun navigateToClimb() {
        Log.d("MainActivity.kt","BottomNavigation tracker clicked")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace<ClimbFragment>(R.id.fragmentContainer)
        transaction.commit()
    }

    private fun navigateToStatistics() {
        Log.d("MainActivity.kt","BottomNavigation tracker clicked")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace<StatisticsFragment>(R.id.fragmentContainer)
        transaction.commit()
    }

    private fun navigateToSettings() {
        Log.d("MainActivity.kt","BottomNavigation tracker clicked")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace<SettingsFragment>(R.id.fragmentContainer)
        transaction.commit()
    }

    /**
     * Checks does serialNo exists in shared preferences
     * @return [Boolean]
     */
    private fun hasSerialNo(): Boolean {
        val prefs = PreferenceHelper.customPrefs(this, PREF_NAME)
        val serialNo = prefs[SERIAL_NO_PREF_NAME, ""]
        return serialNo != ""
    }
}