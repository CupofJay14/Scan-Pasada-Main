package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PassengerHomePage : AppCompatActivity() {
    private lateinit var tvLogout: TextView
    private lateinit var tvViewQueuesButton: TextView
    private lateinit var tvTrackDriversButton: TextView
    private lateinit var tvViewSchedulesButton: TextView
    
    // Bottom Navigation
    private lateinit var ivHomeButton: ImageView
    private lateinit var ivQueueButton: ImageView
    private lateinit var ivScheduleButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_home_page)

        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        tvLogout = findViewById(R.id.tvLogout)
        tvViewQueuesButton = findViewById(R.id.tvViewQueuesButton)
        tvTrackDriversButton = findViewById(R.id.tvTrackDriversButton)
        tvViewSchedulesButton = findViewById(R.id.tvViewSchedulesButton)
        
        // Bottom Navigation
        ivHomeButton = findViewById(R.id.ivHomePageHomeButton)
        ivQueueButton = findViewById(R.id.ivHomePageQueuePageButton)
        ivScheduleButton = findViewById(R.id.ivScheduleButton)
    }
    
    private fun setupClickListeners() {
        tvLogout.setOnClickListener {
            AuthManager.getInstance(this).logout()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        tvViewQueuesButton.setOnClickListener {
            val intent = Intent(this, QueueLandingPage::class.java)
            startActivity(intent)
        }
        
        tvTrackDriversButton.setOnClickListener {
            // TODO: Implement driver tracking
            Toast.makeText(this, "Driver tracking coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        tvViewSchedulesButton.setOnClickListener {
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
    }
}
