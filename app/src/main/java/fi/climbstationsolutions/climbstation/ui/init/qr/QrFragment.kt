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
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.common.util.concurrent.ListenableFuture
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentQrBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Camera codes: https://developer.android.com/codelabs/camerax-getting-started
 */
class QrFragment : Fragment() {
    companion object {
        private const val TAG = "QR"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: FragmentQrBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrBinding.inflate(layoutInflater)

        askPermissions()
        cameraExecutor = Executors.newSingleThreadExecutor()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        context?.let { con ->
            cameraProviderFuture = ProcessCameraProvider.getInstance(con)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
                imageCapture = ImageCapture.Builder().build()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(cameraExecutor, QrAnalyzer { s ->
                            Log.d(TAG, "QR: $s")
                        })
                    }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.apply {
                        unbindAll()
                        bindToLifecycle(
                            viewLifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalyzer
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Use case binding failed", e)
                }

            }, ContextCompat.getMainExecutor(con))
        }
    }

    private fun askPermissions() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            cameraPermissionResult.launch(REQUIRED_PERMISSION)
        }

    }

    private fun allPermissionsGranted(): Boolean {
        val context = context ?: return false
        return ContextCompat.checkSelfPermission(
            context, REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                startCamera()
            } else {
                if(shouldShowRequestPermissionRationale(REQUIRED_PERMISSION)) {
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