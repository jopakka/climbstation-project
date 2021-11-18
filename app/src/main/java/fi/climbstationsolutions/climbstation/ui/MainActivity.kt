package fi.climbstationsolutions.climbstation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.ActivityMainBinding
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.init.InitActivity
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModelFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ClimbStation)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.overflowNavView.setNavigationItemSelectedListener(this)

        /*
            If no serialNo found, then start InitActivity
            else continue with MainActivity
         */
        if (!hasSerialNo()) {
            val initIntent = Intent(this, InitActivity::class.java)
            startActivity(initIntent)
            finish()
        } else {
            setContentView(binding.root)

            initNavigation()
            viewModel.setWeight()

            binding.topAppBar.setOnMenuItemClickListener { menuItem ->
                Log.d("topAppBar", "menu clicked")
                when (menuItem.itemId) {
                    R.id.more -> {
                        if (binding.overflowDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                            binding.overflowDrawerLayout.closeDrawer(GravityCompat.END)
                        } else if (!binding.overflowDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                            binding.overflowDrawerLayout.openDrawer(GravityCompat.END)
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHost.navController

        val navView = binding.bottomNavigation
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(onDestChangedListener)
    }

    private val onDestChangedListener = NavController.OnDestinationChangedListener { _, d, _ ->
        when (d.id) {
            R.id.climbOnFragment, R.id.climbFinishedFragment -> hideBottomNav()
            else -> showBottomNav()
        }
    }

    private fun showBottomNav() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bottomNavigation.visibility = View.GONE
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

    // For top app bar overflow menu items
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                Log.d("MainActivity3 menu item click", "settings clicked")
                return true
            }
            R.id.nav_info -> {
                Log.d("MainActivity3 menu item click", "info clicked")
                return true
            }
        }
        return false
    }
}