package com.example.scanpasada

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.scanpasada.models.QueueDriver
import java.text.SimpleDateFormat
import java.util.*

class DriverMonitoringActivity : AppCompatActivity() {

    private lateinit var lvDrivers: ListView
    private lateinit var tvBackButton: TextView
    private lateinit var tvRefreshButton: TextView
    private lateinit var tvTotalDrivers: TextView
    private val driverList = mutableListOf<QueueDriver>()
    private lateinit var driverAdapter: ArrayAdapter<QueueDriver>
    private lateinit var roleManager: RoleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user has permission to access driver monitoring
        val roleManager = RoleManager.getInstance(this)
        if (!roleManager.canMonitorDrivers()) {
            Toast.makeText(this, "Access denied. Only operators can monitor drivers.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        setContentView(R.layout.activity_driver_monitoring)

        initializeViews()
        setupClickListeners()
        loadDrivers()
    }
    
    private fun initializeViews() {
        lvDrivers = findViewById(R.id.lvDrivers)
        tvBackButton = findViewById(R.id.tvBackButton)
        tvRefreshButton = findViewById(R.id.tvRefreshButton)
        tvTotalDrivers = findViewById(R.id.tvTotalDrivers)
        roleManager = RoleManager.getInstance(this)
        
        driverAdapter = object : ArrayAdapter<QueueDriver>(this, android.R.layout.simple_list_item_2, driverList) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text1 = view.findViewById<TextView>(android.R.id.text1)
                val text2 = view.findViewById<TextView>(android.R.id.text2)
                
                val driver = getItem(position) ?: return view
                text1.text = driver?.driverName
                text2.text = "Plate: ${driver?.plateNumber} | Location: ${String.format("%.4f, %.4f", driver?.latitude ?: 0.0, driver?.longitude ?: 0.0)}"
                
                text1.setTextColor(resources.getColor(android.R.color.black))
                text2.setTextColor(resources.getColor(android.R.color.darker_gray))
                
                return view
            }
        }
        lvDrivers.adapter = driverAdapter
    }
    
    private fun setupClickListeners() {
        tvBackButton.setOnClickListener { finish() }
        
        tvRefreshButton.setOnClickListener {
            loadDrivers()
            Toast.makeText(this, "Driver list refreshed", Toast.LENGTH_SHORT).show()
        }
        
        lvDrivers.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val driver = driverList[position]
            showDriverDetailsDialog(driver)
        }
    }
    
    private fun loadDrivers() {
        // TODO: Load drivers from Supabase
        // For now, add some sample data
        driverList.clear()
        
        driverList.add(QueueDriver("queue1", "driver1", "Juan Dela Cruz", "ABC-123").apply {
            latitude = 14.5995
            longitude = 120.9842
        })
        
        driverList.add(QueueDriver("queue1", "driver2", "Pedro Santos", "XYZ-789").apply {
            latitude = 14.6000
            longitude = 120.9850
        })
        
        driverList.add(QueueDriver("queue2", "driver3", "Maria Lopez", "KLM-456").apply {
            latitude = 14.5980
            longitude = 120.9830
        })
        
        driverAdapter.notifyDataSetChanged()
        tvTotalDrivers.text = "Total Drivers: ${driverList.size}"
    }
    
    private fun showDriverDetailsDialog(driver: QueueDriver) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Driver Details")
        
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val details = "Name: ${driver.driverName}\n" +
                     "Plate Number: ${driver.plateNumber}\n" +
                     "Location: ${String.format("%.6f, %.6f", driver.latitude, driver.longitude)}\n" +
                     "Joined: ${sdf.format(driver.joinedAt)}\n" +
                     "Status: ${if (driver.isActive) "Active" else "Inactive"}"
        
        builder.setMessage(details)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.setNegativeButton("Track Location") { _, _ ->
            // TODO: Open map to track driver location
            Toast.makeText(this, "Location tracking coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        builder.show()
    }
}
