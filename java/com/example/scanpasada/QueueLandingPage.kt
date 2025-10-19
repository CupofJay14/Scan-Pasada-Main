package com.example.scanpasada

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class QueueLandingPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ArrayAdapter<QueueItem>
    private val queueList = mutableListOf<QueueItem>()
    private lateinit var listView: ListView
    private lateinit var tvBackButton: TextView
    private lateinit var tvRefreshButton: TextView
    private lateinit var tvScanQRButton: TextView
    private lateinit var roleManager: RoleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_landing_page)

        initializeViews()
        setupClickListeners()
        loadQueueData()
    }
    
    private fun initializeViews() {
        listView = findViewById(R.id.listViewQueues)
        tvBackButton = findViewById(R.id.tvBackButton)
        tvRefreshButton = findViewById(R.id.tvRefreshButton)
        tvScanQRButton = findViewById(R.id.tvScanQRButton)
        dbHelper = DatabaseHelper(this)
        roleManager = RoleManager.getInstance(this)
    }
    
    private fun setupClickListeners() {
        tvBackButton.setOnClickListener { finish() }
        
        tvRefreshButton.setOnClickListener {
            loadQueueData()
            Toast.makeText(this, "Queue list refreshed", Toast.LENGTH_SHORT).show()
        }
        
        // Only drivers can scan QR codes to join queues
        if (roleManager.canJoinQueues()) {
            tvScanQRButton.visibility = View.VISIBLE
            tvScanQRButton.setOnClickListener {
                val intent = Intent(this, QRScannerActivity::class.java)
                startActivity(intent)
            }
        } else {
            tvScanQRButton.visibility = View.GONE
        }
    }

    private fun loadQueueData() {
        dbHelper.autoDeactivateExpiredQueues() // Auto end expired queues

        queueList.clear()
        val cursor = dbHelper.getAllQueues()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val route = cursor.getString(cursor.getColumnIndexOrThrow("route"))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"))
                val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1
                queueList.add(QueueItem(id, route, startTime, endTime, isActive))
            } while (cursor.moveToNext())
        }
        cursor.close()

        adapter = object : ArrayAdapter<QueueItem>(this, R.layout.queue_list_item, R.id.tvQueueRoute, queueList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.queue_list_item, parent, false)

                val item = getItem(position) ?: return view
                val tvRoute = view.findViewById<TextView>(R.id.tvQueueRoute)
                val tvTime = view.findViewById<TextView>(R.id.tvQueueTime)
                val tvStatus = view.findViewById<TextView>(R.id.tvQueueStatus)
                val ivQR = view.findViewById<ImageView>(R.id.ivQueueQR)
                val btnEndQueue = view.findViewById<Button>(R.id.btnEndQueue)

                tvRoute.text = "Route: ${item?.route}"
                tvTime.text = "Time: ${item?.startTime} - ${item?.endTime}"

                // Update status color
                if (item?.isActive == true) {
                    tvStatus.text = "Active"
                    tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                    btnEndQueue.isEnabled = true
                    btnEndQueue.alpha = 1.0f
                } else {
                    tvStatus.text = "Ended"
                    tvStatus.setTextColor(resources.getColor(android.R.color.darker_gray))
                    btnEndQueue.isEnabled = false
                    btnEndQueue.alpha = 0.5f
                }

                // Generate QR
                try {
                    val encoder = BarcodeEncoder()
                    val qrData = "QUEUE_ID:${item?.id}\nRoute: ${item?.route}\nStart: ${item?.startTime}\nEnd: ${item?.endTime}"
                    val bitmap = encoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 200, 200)
                    ivQR.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }

                // Handle End Queue button - Only operators can end queues
                if (roleManager.canManageQueues()) {
                    btnEndQueue.visibility = View.VISIBLE
                    btnEndQueue.setOnClickListener {
                        dbHelper.updateQueueStatus(item?.id ?: 0, false)
                        Toast.makeText(this@QueueLandingPage, "Queue ended manually.", Toast.LENGTH_SHORT).show()
                        loadQueueData() // Refresh list
                    }
                } else {
                    btnEndQueue.visibility = View.GONE
                }

                return view
            }
        }

        listView.adapter = adapter
    }
}
