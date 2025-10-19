package com.example.scanpasada

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class QRScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private val CAMERA_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        initializeViews()
        checkCameraPermission()
    }

    private fun initializeViews() {
        barcodeView = findViewById(R.id.barcodeView)
        barcodeView.decodeContinuous(callback)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            startScanning()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun startScanning() {
        barcodeView.resume()
    }

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            result.text?.let { handleQRCodeResult(it) }
        }
    }

    private fun handleQRCodeResult(qrData: String) {
        // Parse QR code data
        if (qrData.startsWith("QUEUE_ID:")) {
            val lines = qrData.split("\n")
            val queueId = lines[0].replace("QUEUE_ID:", "")
            val route = lines[1].replace("Route: ", "")
            val startTime = lines[2].replace("Start: ", "")
            val endTime = lines[3].replace("End: ", "")

            try {
                // Driver details would normally come from the logged-in user profile
                val auth = AuthManager.getInstance(this)
                val driverId = auth.getCurrentUser()?.id ?: ""
                val driverName = auth.getCurrentUser()?.fullName ?: "Driver"
                val plateNumber = auth.getCurrentUser()?.plateNumber ?: ""

                SupabaseService().joinQueue(queueId, driverId, driverName, plateNumber)

                Toast.makeText(this, "Successfully joined queue!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to join queue", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }
}