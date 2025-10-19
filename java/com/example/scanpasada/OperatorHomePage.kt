package com.example.scanpasada

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*

class OperatorHomePage : AppCompatActivity() {

    companion object {
        val queueList = mutableListOf<QueueItem>()
    }
    
    private lateinit var tvLogout: TextView
    private lateinit var tvCreateQueueButton: TextView
    private lateinit var tvScheduleManagementButton: TextView
    private lateinit var tvDriverMonitoringButton: TextView
    
    // Bottom Navigation
    private lateinit var ivHomeButton: ImageView
    private lateinit var ivQueueButton: ImageView
    private lateinit var ivDriverMonitoringButton: ImageView
    private lateinit var ivScheduleButton: ImageView
    private lateinit var ivProfileButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_home_page)

        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        tvLogout = findViewById(R.id.tvLogout)
        tvCreateQueueButton = findViewById(R.id.tvCreateQueueButton)
        tvScheduleManagementButton = findViewById(R.id.tvScheduleManagementButton)
        tvDriverMonitoringButton = findViewById(R.id.tvDriverMonitoringButton)
        
        // Bottom Navigation
        ivHomeButton = findViewById(R.id.ivHomePageHomeButton)
        ivQueueButton = findViewById(R.id.ivHomePageQueuePageButton)
        ivDriverMonitoringButton = findViewById(R.id.ivDriverMonitoringButton)
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

        tvCreateQueueButton.setOnClickListener { showCreateQueueDialog() }
        
        tvScheduleManagementButton.setOnClickListener {
            val intent = Intent(this, ScheduleManagementActivity::class.java)
            startActivity(intent)
        }
        
        tvDriverMonitoringButton.setOnClickListener {
            val intent = Intent(this, DriverMonitoringActivity::class.java)
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
        
        ivDriverMonitoringButton.setOnClickListener {
            val intent = Intent(this, DriverMonitoringActivity::class.java)
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

    private fun showCreateQueueDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_queue, null)
        builder.setView(dialogView)

        val etStartTime = dialogView.findViewById<EditText>(R.id.etStartTime)
        val etEndTime = dialogView.findViewById<EditText>(R.id.etEndTime)
        val etRoute = dialogView.findViewById<EditText>(R.id.etRoute)
        val switchActive = dialogView.findViewById<Switch>(R.id.switchActive)
        val btnGenerateQR = dialogView.findViewById<Button>(R.id.btnGenerateQR)
        val ivQRCode = dialogView.findViewById<ImageView>(R.id.ivQRCode)

        val dialog = builder.create()
        dialog.show()

        val calendar = Calendar.getInstance()

        etStartTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, h, m ->
                etStartTime.setText(String.format("%02d:%02d", h, m))
            }, hour, minute, true).show()
        }

        etEndTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, h, m ->
                etEndTime.setText(String.format("%02d:%02d", h, m))
            }, hour, minute, true).show()
        }

        btnGenerateQR.setOnClickListener {
            val startTime = etStartTime.text.toString()
            val endTime = etEndTime.text.toString()
            val route = etRoute.text.toString()
            val isActive = switchActive.isChecked

            if (startTime.isEmpty() || endTime.isEmpty() || route.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbHelper = DatabaseHelper(this)
            val success = dbHelper.insertQueue(route, startTime, endTime, isActive)

            if (success) {
                Toast.makeText(this, "Queue saved to database!", Toast.LENGTH_SHORT).show()

                // Optional QR generation
                val queueData = "Route: $route\nStart: $startTime\nEnd: $endTime"
                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.encodeBitmap(queueData, BarcodeFormat.QR_CODE, 400, 400)
                    ivQRCode.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }

                dialog.dismiss()
            } else {
                Toast.makeText(this, "Error saving queue!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sortQueueList() {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        queueList.sortWith { q1, q2 ->
            try {
                sdf.parse(q1.startTime)?.compareTo(sdf.parse(q2.startTime)) ?: 0
            } catch (e: Exception) {
                0
            }
        }
    }

    private fun scheduleQueueEnd(item: QueueItem) {
        val handler = Handler()
        try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val end = sdf.parse(item.endTime)
            val now = Date()
            val delay = end?.time?.minus(now.time) ?: 0
            if (delay > 0) {
                handler.postDelayed({
                    // Note: QueueItem.isActive is a val, so we can't modify it directly
                    // This would need to be handled through the database or a different approach
                    Toast.makeText(this, "Queue for ${item.route} has ended.", Toast.LENGTH_SHORT).show()
                }, delay)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
