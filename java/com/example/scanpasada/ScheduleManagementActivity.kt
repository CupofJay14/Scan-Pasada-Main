package com.example.scanpasada

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.scanpasada.models.Schedule
import java.text.SimpleDateFormat
import java.util.*

class ScheduleManagementActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tvAddScheduleButton: TextView
    private lateinit var tvBackButton: TextView
    private lateinit var lvSchedules: ListView
    private val scheduleList = mutableListOf<Schedule>()
    private lateinit var scheduleAdapter: ArrayAdapter<Schedule>
    private lateinit var roleManager: RoleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_management)

        initializeViews()
        setupClickListeners()
        loadSchedules()
    }
    
    private fun initializeViews() {
        calendarView = findViewById(R.id.calendarView)
        tvAddScheduleButton = findViewById(R.id.tvAddScheduleButton)
        tvBackButton = findViewById(R.id.tvBackButton)
        lvSchedules = findViewById(R.id.lvSchedules)
        roleManager = RoleManager.getInstance(this)
        
        scheduleAdapter = object : ArrayAdapter<Schedule>(this, android.R.layout.simple_list_item_1, scheduleList) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                val schedule = getItem(position) ?: return view
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                textView.text = "${schedule?.title} - ${sdf.format(schedule?.date)}"
                textView.setTextColor(resources.getColor(android.R.color.black))
                return view
            }
        }
        lvSchedules.adapter = scheduleAdapter
    }
    
    private fun setupClickListeners() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            showScheduleForDate(selectedDate.time)
        }
        
        // Only operators can add schedules
        if (roleManager.canManageSchedules()) {
            tvAddScheduleButton.visibility = View.VISIBLE
            tvAddScheduleButton.setOnClickListener { showAddScheduleDialog() }
        } else {
            tvAddScheduleButton.visibility = View.GONE
        }
        
        tvBackButton.setOnClickListener { finish() }
        
        lvSchedules.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val schedule = scheduleList[position]
            showScheduleDetailsDialog(schedule)
        }
    }
    
    private fun loadSchedules() {
        // TODO: Load schedules from Supabase
        // For now, add some sample data
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        scheduleList.add(Schedule("Morning Route", "Regular morning jeepney service", cal.time, "06:00", "08:00", 1L))
        
        cal.add(Calendar.DAY_OF_MONTH, 1)
        scheduleList.add(Schedule("Evening Route", "Regular evening jeepney service", cal.time, "17:00", "19:00", 1L))
        
        scheduleAdapter.notifyDataSetChanged()
    }
    
    private fun showScheduleForDate(date: Date) {
        val daySchedules = mutableListOf<Schedule>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateStr = sdf.format(date)
        
        for (schedule in scheduleList) {
            if (sdf.format(schedule.date) == selectedDateStr) {
                daySchedules.add(schedule)
            }
        }
        
        if (daySchedules.isEmpty()) {
            Toast.makeText(this, "No schedules for this date", Toast.LENGTH_SHORT).show()
        } else {
            val sb = StringBuilder()
            for (schedule in daySchedules) {
                sb.append("${schedule.title} (${schedule.startTime} - ${schedule.endTime})\n")
            }
            Toast.makeText(this, "Schedules for $selectedDateStr:\n$sb", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showAddScheduleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Schedule")
        
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_schedule, null)
        builder.setView(dialogView)
        
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val etStartTime = dialogView.findViewById<EditText>(R.id.etStartTime)
        val etEndTime = dialogView.findViewById<EditText>(R.id.etEndTime)
        val dpDate = dialogView.findViewById<DatePicker>(R.id.dpDate)
        
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
        
        dialogView.findViewById<Button>(R.id.btnSaveSchedule).setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            val startTime = etStartTime.text.toString()
            val endTime = etEndTime.text.toString()
            
            if (title.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val selectedDate = Calendar.getInstance().apply {
                set(dpDate.year, dpDate.month, dpDate.dayOfMonth)
            }
            
            val newSchedule = Schedule(title, description, selectedDate.time, startTime, endTime, 1L)
            scheduleList.add(newSchedule)
            scheduleAdapter.notifyDataSetChanged()
            
            Toast.makeText(this, "Schedule added successfully!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        dialogView.findViewById<Button>(R.id.btnCancelSchedule).setOnClickListener { dialog.dismiss() }
    }
    
    private fun showScheduleDetailsDialog(schedule: Schedule) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Schedule Details")
        
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val details = "Title: ${schedule.title}\n" +
                     "Description: ${schedule.description}\n" +
                     "Date: ${sdf.format(schedule.date)}\n" +
                     "Time: ${schedule.startTime} - ${schedule.endTime}"
        
        builder.setMessage(details)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.setNegativeButton("Delete") { _, _ ->
            scheduleList.remove(schedule)
            scheduleAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Schedule deleted", Toast.LENGTH_SHORT).show()
        }
        
        builder.show()
    }
}
