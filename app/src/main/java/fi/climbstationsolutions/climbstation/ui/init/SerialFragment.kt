package fi.climbstationsolutions.climbstation.ui.init

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentSerialBinding
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.set
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME
import fi.climbstationsolutions.climbstation.ui.init.qr.QrCamera
import kotlinx.coroutines.launch

class SerialFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener {
    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_SERIAL = "Climbstation.serial"
    }

    private lateinit var binding: FragmentSerialBinding
    private val viewModel: InitViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var qrCamera: QrCamera

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSerialBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        bottomSheetBehavior = BottomSheetBehavior.from(binding.sheetLayout)

        initUI()
        setBottomSheetVisibility(false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::qrCamera.isInitialized) {
            qrCamera.closeCamera()
        }
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        val newState =
            if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.state = newState
    }

    override fun onGlobalLayout() {
        binding.sheetLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
        val hidden = binding.sheetLayout.getChildAt(2)
        bottomSheetBehavior.peekHeight = hidden.top
    }

    private fun submitSerial(serial: String) {
        val result = Intent().putExtra(EXTRA_SERIAL, serial)
        activity?.setResult(Activity.RESULT_OK, result)
        activity?.finish()
    }

    /**
     * Initializes UI elements and observers
     */
    private fun initUI() {
        binding.apply {
            etSerialNo.addTextChangedListener(textOnChange)
            etSerialNo.setOnEditorActionListener(keyboardActionListener)
            btnContinue.setOnClickListener(btnClickListener)
            sheetLayout.viewTreeObserver.addOnGlobalLayoutListener(this@SerialFragment)
            sheetLayout.setOnClickListener(this@SerialFragment)
        }
        viewModel.loading.observe(viewLifecycleOwner, loadingObserver)
        viewModel.serial.observe(viewLifecycleOwner, serialObserver)
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.sheetLayout -> setBottomSheetVisibility(true)
        }
    }

    private val keyboardActionListener = TextView.OnEditorActionListener { textView, actionId, _ ->
        var handled = false

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            keyboardStatus(view = textView)
            handled = true
        }

        return@OnEditorActionListener handled
    }

    /**
     * ClickListener for button
     */
    private val btnClickListener = View.OnClickListener {
        testSerial(binding.etSerialNo.text.toString())
    }

    /**
     * Tests is given serial number correct
     */
    private fun testSerial(serial: String) {
        editableStatus(false)
        keyboardStatus(view = binding.etSerialNo)
        lifecycleScope.launch {
            val error = viewModel.testSerialNo(serial)
            if (error != null) showToast(error)
        }
    }

    private fun editableStatus(value: Boolean) {
        binding.apply {
            btnContinue.isClickable = value
            etSerialNo.isEnabled = value
        }
    }

    private fun keyboardStatus(show: Boolean = false, view: View) {
        val con = context ?: return
        val imm = con.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } else {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private val loadingObserver = Observer<Boolean> {
        if (it) return@Observer
        askPermissions()
        editableStatus(true)
    }

    private val serialObserver = Observer<String> {
        if (it == null) return@Observer
        submitSerial(it)
    }

    private val textOnChange = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {
            binding.btnContinue.isEnabled = cs.trim().isNotEmpty()
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    /**
     * Opens camera
     */
    private fun startCamera() {
        context?.let {
            val qrCamera = QrCamera(it, binding.viewFinder, viewLifecycleOwner)
            qrCamera.startCamera { qr ->
                testSerial(qr)
                qrCamera.closeCamera()
            }
        }
    }

    /**
     * Asks permissions if not already given
     */
    private fun askPermissions() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            cameraPermissionResult.launch(CAMERA_PERMISSION)
        }
    }

    /**
     * Checks if [CAMERA_PERMISSION] is granted
     */
    private fun allPermissionsGranted(): Boolean {
        val context = context ?: return false
        return ContextCompat.checkSelfPermission(
            context, CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * ActivityResult which handles permission result
     */
    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                startCamera()
            } else {
                if (shouldShowRequestPermissionRationale(CAMERA_PERMISSION)) {
                    showCameraAlertDialog()
                } else {
                    Toast.makeText(
                        context, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT
                    ).show()
                    setBottomSheetVisibility(true)
                }
            }
        }

    /**
     * Shows informative AlertDialog to user why app asks camera permission
     */
    private fun showCameraAlertDialog() {
        val context = context ?: return
        val builder = AlertDialog.Builder(context)
            .setTitle(R.string.camera_alert_title)
            .setMessage(R.string.camera_alert_message)
            .setPositiveButton(R.string.camera_alert_positive) { _, _ ->
                askPermissions()
            }
            .setNegativeButton(R.string.no) { d, _ ->
                d.cancel()
                setBottomSheetVisibility(true)
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showToast(text: String) {
        context?.let {
            Toast.makeText(it, text, Toast.LENGTH_SHORT).show()
        }
    }
}