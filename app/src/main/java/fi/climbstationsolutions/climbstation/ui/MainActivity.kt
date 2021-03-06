package fi.climbstationsolutions.climbstation.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ExpandableListAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.ActivityMainBinding
import fi.climbstationsolutions.climbstation.services.ClimbStationService
import fi.climbstationsolutions.climbstation.sharedprefs.*
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.get
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.set
import fi.climbstationsolutions.climbstation.ui.init.GetSerial
import fi.climbstationsolutions.climbstation.utils.ExpandableListData.data
import fi.climbstationsolutions.climbstation.utils.MenuActions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // for drawer menu
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(this)
    }
    private val infoViewModel: InfoPopupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ClimbStation)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initSharedPref()

        // sets ClimbStation drawer menu logo to expandableListView
        val listHeaderView =
            layoutInflater.inflate(R.layout.overflow_menu_header_layout, binding.root, false)
        binding.expendableList.addHeaderView(listHeaderView)

        if (!ClimbStationService.SERVICE_RUNNING && !hasSerialNo()) {
            getSerialNumber.launch(null)
        }

        setContentView(binding.root)

        initNavigation()
        viewModel.setWeight()
        setupCustomExpandableList()

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.more -> {
                    binding.overflowDrawerLayout.openDrawer(GravityCompat.END)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onBackPressed() {
        binding.overflowDrawerLayout.apply {
            if (isDrawerOpen(GravityCompat.END)) {
                closeDrawer(GravityCompat.END)
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun initSharedPref() {
        val pref = PreferenceHelper.customPrefs(this, PREF_NAME)
        if(pref.get<Int>(SPEED_PREF_NAME) == -1) {
            pref[SPEED_PREF_NAME] = 8
        }
    }

    private val getSerialNumber = registerForActivityResult(GetSerial()) {
        it ?: return@registerForActivityResult

        saveSerialNo(it)
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
            R.id.climbFinishedFragment -> hideBottomNav()
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

    private fun saveSerialNo(serial: String) {
        val prefs = PreferenceHelper.customPrefs(this, PREF_NAME)
        prefs[SERIAL_NO_PREF_NAME] = serial
    }

    /**
     * Sets up expandable list for ExpandableListView in the side drawer menu
     * Includes onGroupClick and onChildClick listeners
     */
    private fun setupCustomExpandableList() {
        val listData = data
        titleList = ArrayList(listData.keys)
        adapter = CustomExpandableListAdapter(
            this,
            titleList as ArrayList<String>,
            listData,
            viewModel
        )
        binding.expendableList.setAdapter(adapter)

        binding.expendableList.setOnGroupClickListener { _, _, position: Int, _ ->
            val groupKey =
                (listData.filterValues { it == listData[(titleList as ArrayList<String>)[position]]!! }.keys).elementAt(
                    0
                )
            if (groupKey == "Connect") {
                getSerialNumber.launch(null)
            }
            false
        }

        binding.expendableList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val childItem =
                listData[(titleList as ArrayList<String>)[groupPosition]]!![childPosition]

            val groupKey =
                (listData.filterValues { it == listData[(titleList as ArrayList<String>)[groupPosition]]!! }.keys).elementAt(
                    0
                )

            when (childItem) {
                "Bodyweight" -> {
                    MenuActions().updateUserWeight(this) {
                        val weight = it.toFloatOrNull()
                        viewModel.setWeight(weight) {
                            (adapter as CustomExpandableListAdapter).notifyDataSetChanged()
                        }
                    }
                }
                "Machine speed" -> {
                    MenuActions().updateSpeed(this) {
                        val speed = it
                        viewModel.setSpeed(this, speed) {
                            (adapter as CustomExpandableListAdapter).notifyDataSetChanged()
                        }
                    }
                }
                "Text to speech" -> {
                    MenuActions().toggleTts(this)
                    (adapter as CustomExpandableListAdapter).notifyDataSetChanged()
                }
                else -> {
                    Log.d("MainActivity_menuChildClick", "No actions set for child: $childItem")
                }
            }
            when (groupKey) {
                "Info" -> {
                    MenuActions().showInfoPopup(childItem, this, infoViewModel)
                }
                else -> {
                    Log.d("MainActivity_menuChildClick", "No actions set for group: $groupKey")
                }
            }
            false
        }
    }

}