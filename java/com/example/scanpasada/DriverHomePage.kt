package com.example.scanpasada

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class DriverHomePage : AppCompatActivity() {
    private lateinit var tvLogout: TextView
    private lateinit var tvJoinQueueButton: TextView
    private lateinit var tvQueuesButton: TextView
    private lateinit var tvSchedulesButton: TextView
    private lateinit var tvLocationStatus: TextView
    
    // Bottom Navigation
    private lateinit var ivHomeButton: ImageView
    private lateinit var ivQueueButton: ImageView
    private lateinit var ivScheduleButton: ImageView
    private lateinit var ivProfileButton: ImageView
    
    // Location Services - temporarily disabled
    private val LOCATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_home_page)

        initializeViews()
        setupClickListeners()
        checkLocationPermission()
        initializeLocationService()
    }
    
    private fun initializeViews() {
        tvLogout = findViewById(R.id.tvLogout)
        tvJoinQueueButton = findViewById(R.id.tvJoinQueueButton)
        tvQueuesButton = findViewById(R.id.tvQueuesButton)
        tvSchedulesButton = findViewById(R.id.tvSchedulesButton)
        tvLocationStatus = findViewById(R.id.tvLocationStatus)
        
        // Bottom Navigation
        ivHomeButton = findViewById(R.id.ivHomePageHomeButton)
        ivQueueButton = findViewById(R.id.ivHomePageQueuePageButton)
        ivScheduleButton = findViewById(R.id.ivScheduleButton)
        ivProfileButton = findViewById(R.id.ivHomePageProfileButton)
    }
    
    private fun setupClickListeners() {
        tvLogout.setOnClickListener {
            AuthManager.getInstance(this).logout()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        tvJoinQueueButton.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }
        
        tvQueuesButton.setOnClickListener {
            val intent = Intent(this, QueueLandingPage::class.java)
            startActivity(intent)
        }
        
        tvSchedulesButton.setOnClickListener {
            val intent = Intent(this, ScheduleManagementActivity::class.java)
            startActivity(intent)
        }
        
        // Bottom Navigation
        ivHomeButton.setOnClickListener {
            // Already on home page
        }
        
        ivQueueButton.setOnClickListener {
            val intent = Intent(this, QueueLandingPage::class.java)
            startActivity(intent)
        }
        
        ivScheduleButton.setOnClickListener {
            val intent = Intent(this, ScheduleManagementActivity::class.java)
            startActivity(intent)
        }
        
        ivProfileButton.setOnClickListener {
            // TODO: Navigate to profile page
            Toast.makeText(this, "Profile page coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST)
        }
    }
    
    private fun initializeLocationService() {
        // Location service temporarily disabled
        tvLocationStatus.text = "📍 Location sharing inactive"
        tvLocationStatus.setTextColor(resources.getColor(android.R.color.darker_gray))
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocationService()
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Location service cleanup temporarily disabled
    }
}
