package fi.climbstationsolutions.climbstation.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ExpandableListAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.CustomExpandableListAdapter
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.databinding.ActivityMainBinding
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.init.InitActivity
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModel
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModelFactory
import fi.climbstationsolutions.climbstation.utils.ExpandableListData.data
import fi.climbstationsolutions.climbstation.utils.MenuActions
import kotlinx.coroutines.*
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + parentJob)

    // for drawer menu
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ClimbStation)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

//        binding.overflowNavView.setNavigationItemSelectedListener(this)

        // sets ClimbStation drawer menu logo to expandableListView
        val listHeaderView =
            layoutInflater.inflate(R.layout.overflow_menu_header_layout, null, false)
        binding.expendableList.addHeaderView(listHeaderView)

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
                            setupCustomExpandableList()
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
            R.id.climbOnFragment,
            R.id.climbFinishedFragment,
            R.id.climbHistory -> hideBottomNav()
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

    private fun setupCustomExpandableList() {
        if (binding.expendableList != null) {
            val listData = data
            Log.d("listData1", "listData1: ${listData}")
            Log.d("HashMap_data", "data: $listData")
            titleList = ArrayList(listData.keys)
            adapter = CustomExpandableListAdapter(
                this,
                titleList as ArrayList<String>,
                listData,
                viewModel
            )
            binding.expendableList.setAdapter(adapter)

            binding.expendableList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                val childItem =
                    listData[(titleList as ArrayList<String>)[groupPosition]]!![childPosition]
                Log.d("MainActivity_menuChildClick", "childItem: $childItem")
                if (childItem == "Bodyweight") {
                    MenuActions().updateUserWeight(this) {
                        val weight = it.toFloatOrNull()
                        viewModel.setWeight(weight) {
                            (adapter as CustomExpandableListAdapter).notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.d("MainActivity_menuChildClick", "No actions set for $childItem")
                }
                false
            }
        }
    }
}