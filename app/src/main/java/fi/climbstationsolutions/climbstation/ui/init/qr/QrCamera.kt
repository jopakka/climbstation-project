package fi.climbstationsolutions.climbstation.ui.init.qr

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrCamera(
    private val context: Context,
    private val viewFinder: PreviewView,
    private val lifecycleOwner: LifecycleOwner
) {
    companion object {
        private const val TAG = "QrCamera"
    }

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    fun startCamera(listener: (String) -> Unit) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = getPreview()
            imageCapture = ImageCapture.Builder().build()
            val imageAnalyzer = getAnalyzer(listener)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.apply {
                    unbindAll()
                    bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalyzer
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun closeCamera() {
        if (this::cameraExecutor.isInitialized) cameraExecutor.shutdown()
    }

    private fun getPreview() = Preview.Builder().build().also {
        it.setSurfaceProvider(viewFinder.surfaceProvider)
    }

    private fun getAnalyzer(listener: (String) -> Unit) = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build().also {
            it.setAnalyzer(cameraExecutor, QrAnalyzer { s ->
                listener(s)
            })
        }
}