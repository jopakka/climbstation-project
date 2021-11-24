package fi.climbstationsolutions.climbstation.ui.init.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentQrBinding

/**
 * Camera codes: https://developer.android.com/codelabs/camerax-getting-started
 */
class QrFragment : Fragment() {
    companion object {
        private const val TAG = "QrFragment"
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

    private lateinit var binding: FragmentQrBinding
    private lateinit var qrCamera: QrCamera

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrBinding.inflate(layoutInflater)

        askPermissions()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::qrCamera.isInitialized) {
            qrCamera.closeCamera()
        }
    }

    /**
     * Opens camera
     */
    private fun startCamera() {
        context?.let {
            val qrCamera = QrCamera(it, binding.viewFinder, viewLifecycleOwner)
            qrCamera.startCamera { qr ->
                Log.d(TAG, "QR-code: $qr")
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
                        context,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT
                    ).show()
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
            }

        val dialog = builder.create()
        dialog.show()
    }
}