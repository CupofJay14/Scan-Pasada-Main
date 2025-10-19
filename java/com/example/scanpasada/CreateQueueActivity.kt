package com.example.scanpasada

import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

class CreateQueueActivity : AppCompatActivity() {

    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etRoute: EditText
    private lateinit var switchActive: Switch
    private lateinit var btnGenerateQR: Button
    private lateinit var ivQRCode: ImageView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_create_queue)

        dbHelper = DatabaseHelper(this)

        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        etRoute = findViewById(R.id.etRoute)
        switchActive = findViewById(R.id.switchActive)
        btnGenerateQR = findViewById(R.id.btnGenerateQR)
        ivQRCode = findViewById(R.id.ivQRCode)

        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }

        btnGenerateQR.setOnClickListener {
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()
            val route = etRoute.text.toString().trim()
            val isActive = switchActive.isChecked

            if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(route)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.insertQueue(route, startTime, endTime, isActive)
            if (success) {
                Toast.makeText(this, "Queue created successfully!", Toast.LENGTH_SHORT).show()

                val queueData = "Route: $route\n" +
                        "Start: $startTime\n" +
                        "End: $endTime\n" +
                        "Active: $isActive"

                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.encodeBitmap(queueData, BarcodeFormat.QR_CODE, 400, 400)
                    ivQRCode.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(this, "Failed to create queue.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTimePicker(targetEditText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val amPm = if (selectedHour >= 12) "PM" else "AM"
                    var hourIn12 = selectedHour % 12
                    if (hourIn12 == 0) hourIn12 = 12

                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12, selectedMinute, amPm)
                    targetEditText.setText(formattedTime)
                },
                hour,
                minute,
                false
        )
        timePickerDialog.show()
    }
}
