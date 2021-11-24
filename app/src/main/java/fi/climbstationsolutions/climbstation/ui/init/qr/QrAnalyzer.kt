package fi.climbstationsolutions.climbstation.ui.init.qr

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QrAnalyzer(private val listener: (String) -> Unit) : ImageAnalysis.Analyzer {

    /**
     * Analyzes [imageProxy] and checks is it [Int].
     * Then sends it to [listener]
     */
    override fun analyze(imageProxy: ImageProxy) {
        val scanner = getScanner(getOptions())
        scanner.process(getInputImage(imageProxy))
            .addOnSuccessListener { barcodes ->
                barcodes.forEach {
                    val serial = it.rawValue
                    if (!serial.isNullOrEmpty() && serial.toIntOrNull() != null) {
                        listener(serial)
                    }

                }
            }

        imageProxy.close()
    }

    /**
     * Makes [InputImage] from [imageProxy]
     */
    private fun getInputImage(imageProxy: ImageProxy): InputImage {
        val buffer = imageProxy.planes[0].buffer
        return InputImage.fromByteBuffer(
            buffer,
            imageProxy.width,
            imageProxy.height,
            imageProxy.imageInfo.rotationDegrees,
            InputImage.IMAGE_FORMAT_NV21
        )
    }

    /**
     * Options for barcodeScanner.
     * Detects only QR-codes
     */
    private fun getOptions() = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

    private fun getScanner(options: BarcodeScannerOptions) = BarcodeScanning.getClient(options)
}