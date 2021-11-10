package fi.climbstationsolutions.climbstation.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.replace
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.BodyWeight
import fi.climbstationsolutions.climbstation.database.SettingsDao
import fi.climbstationsolutions.climbstation.databinding.ActivityMainBinding
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.climb.ClimbFragment
import fi.climbstationsolutions.climbstation.ui.init.InitActivity
import fi.climbstationsolutions.climbstation.ui.settings.SettingsFragment
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var settingsDao: SettingsDao
    private lateinit var binding: ActivityMainBinding

    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var userBodyWeightDefault: Float = 70.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ClimbStation)
        super.onCreate(savedInstanceState)
        super.onPostResume()
        binding = ActivityMainBinding.inflate(layoutInflater)

        settingsDao = AppDatabase.get(applicationContext).settingsDao()

        ioScope.launch {
            val userWeight = settingsDao.getBodyWeightById(1)
            if (userWeight == null) {
                settingsDao.insertUserBodyWeight(BodyWeight(1, userBodyWeightDefault))
            }
        }

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
            setContentView(binding.root)

            initNavigation()
        }
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHost.navController

        val navView = binding.bottomNavigation
        navView.setupWithNavController(navController)
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