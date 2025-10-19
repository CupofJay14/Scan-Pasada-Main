package com.example.scanpasada

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "User DB.db"
        private const val DATABASE_VERSION = 2

        // Passenger table
        private const val TABLE_PASSENGERS = "passengers"
        private const val PASSENGER_ID = "id"
        private const val PASSENGER_FULL_NAME = "full_name"
        private const val PASSENGER_AGE = "age"
        private const val PASSENGER_ADDRESS = "address"
        private const val PASSENGER_PASSWORD = "password"
        private const val PASSENGER_CREATED_AT = "created_at"

        // Operator table
        private const val TABLE_OPERATORS = "operators"
        private const val OPERATOR_ID = "id"
        private const val OPERATOR_FULL_NAME = "full_name"
        private const val OPERATOR_PHONE = "phone"
        private const val OPERATOR_TERMINAL_NAME = "terminal_name"
        private const val OPERATOR_TERMINAL_LOCATION = "terminal_location"
        private const val OPERATOR_PASSWORD = "password"
        private const val OPERATOR_CREATED_AT = "created_at"

        // Driver table
        private const val TABLE_DRIVERS = "drivers"
        private const val DRIVER_ID = "id"
        private const val DRIVER_FULL_NAME = "full_name"
        private const val DRIVER_PHONE = "phone"
        private const val DRIVER_LICENSE_NUMBER = "license_number"
        private const val DRIVER_EXPIRY_DATE = "expiry_date"
        private const val DRIVER_PLATE_NUMBER = "plate_number"
        private const val DRIVER_PASSWORD = "password"
        private const val DRIVER_CREATED_AT = "created_at"

        // Queue table
        private const val TABLE_QUEUES = "queues"
        private const val QUEUE_ID = "id"
        private const val QUEUE_ROUTE = "route"
        private const val QUEUE_START_TIME = "start_time"
        private const val QUEUE_END_TIME = "end_time"
        private const val QUEUE_IS_ACTIVE = "is_active"
        private const val QUEUE_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create passengers table
        val createPassengersTable = "CREATE TABLE $TABLE_PASSENGERS (" +
                "$PASSENGER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$PASSENGER_FULL_NAME TEXT NOT NULL, " +
                "$PASSENGER_AGE INTEGER NOT NULL, " +
                "$PASSENGER_ADDRESS TEXT NOT NULL, " +
                "$PASSENGER_PASSWORD TEXT NOT NULL, " +
                "$PASSENGER_CREATED_AT TEXT" +
                ")"
        db.execSQL(createPassengersTable)

        // Create operators table
        val createOperatorsTable = "CREATE TABLE $TABLE_OPERATORS (" +
                "$OPERATOR_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$OPERATOR_FULL_NAME TEXT NOT NULL, " +
                "$OPERATOR_PHONE TEXT UNIQUE NOT NULL, " +
                "$OPERATOR_TERMINAL_NAME TEXT NOT NULL, " +
                "$OPERATOR_TERMINAL_LOCATION TEXT NOT NULL, " +
                "$OPERATOR_PASSWORD TEXT NOT NULL, " +
                "$OPERATOR_CREATED_AT TEXT" +
                ")"
        db.execSQL(createOperatorsTable)

        // Create drivers table
        val createDriversTable = "CREATE TABLE $TABLE_DRIVERS (" +
                "$DRIVER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$DRIVER_FULL_NAME TEXT NOT NULL, " +
                "$DRIVER_PHONE TEXT UNIQUE NOT NULL, " +
                "$DRIVER_LICENSE_NUMBER TEXT NOT NULL, " +
                "$DRIVER_EXPIRY_DATE TEXT NOT NULL, " +
                "$DRIVER_PLATE_NUMBER TEXT NOT NULL, " +
                "$DRIVER_PASSWORD TEXT NOT NULL, " +
                "$DRIVER_CREATED_AT TEXT" +
                ")"
        db.execSQL(createDriversTable)

        // Create queues table
        val createQueuesTable = "CREATE TABLE $TABLE_QUEUES (" +
                "$QUEUE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$QUEUE_ROUTE TEXT NOT NULL, " +
                "$QUEUE_START_TIME TEXT NOT NULL, " +
                "$QUEUE_END_TIME TEXT NOT NULL, " +
                "$QUEUE_IS_ACTIVE INTEGER NOT NULL, " +
                "$QUEUE_CREATED_AT TEXT" +
                ")"
        db.execSQL(createQueuesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PASSENGERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OPERATORS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DRIVERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUEUES")
        onCreate(db)
    }

    // Insert passenger
    fun insertPassenger(fullName: String, age: Int, address: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(PASSENGER_FULL_NAME, fullName)
            put(PASSENGER_AGE, age)
            put(PASSENGER_ADDRESS, address)
            put(PASSENGER_PASSWORD, hashPassword(password))
            put(PASSENGER_CREATED_AT, getCurrentDateTime())
        }
        val result = db.insert(TABLE_PASSENGERS, null, values)
        db.close()
        return result != -1L
    }

    // Insert operator
    fun insertOperator(fullName: String, phone: String, terminalName: String, terminalLocation: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(OPERATOR_FULL_NAME, fullName)
            put(OPERATOR_PHONE, phone)
            put(OPERATOR_TERMINAL_NAME, terminalName)
            put(OPERATOR_TERMINAL_LOCATION, terminalLocation)
            put(OPERATOR_PASSWORD, hashPassword(password))
            put(OPERATOR_CREATED_AT, getCurrentDateTime())
        }
        val result = db.insert(TABLE_OPERATORS, null, values)
        db.close()
        return result != -1L
    }

    // Insert driver
    fun insertDriver(fullName: String, phone: String, licenseNumber: String, expiryDate: String, plateNumber: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(DRIVER_FULL_NAME, fullName)
            put(DRIVER_PHONE, phone)
            put(DRIVER_LICENSE_NUMBER, licenseNumber)
            put(DRIVER_EXPIRY_DATE, expiryDate)
            put(DRIVER_PLATE_NUMBER, plateNumber)
            put(DRIVER_PASSWORD, hashPassword(password))
            put(DRIVER_CREATED_AT, getCurrentDateTime())
        }
        val result = db.insert(TABLE_DRIVERS, null, values)
        db.close()
        return result != -1L
    }

    // Insert a new queue
    fun insertQueue(route: String, startTime: String, endTime: String, isActive: Boolean): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(QUEUE_ROUTE, route)
            put(QUEUE_START_TIME, startTime)
            put(QUEUE_END_TIME, endTime)
            put(QUEUE_IS_ACTIVE, if (isActive) 1 else 0)
            put(QUEUE_CREATED_AT, getCurrentDateTime())
        }

        var result = -1L
        try {
            result = db.insertOrThrow(TABLE_QUEUES, null, values)
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Error inserting queue: ${e.message}")
        }
        db.close()
        return result != -1L
    }

    // Fetch all queues sorted by start time
    fun getAllQueues(): Cursor {
        autoDeactivateExpiredQueues()
        val db = this.readableDatabase
        return db.query(TABLE_QUEUES, null, null, null, null, null, "$QUEUE_START_TIME ASC")
    }

    // Update queue active status (for auto end)
    fun updateQueueStatus(id: Int, isActive: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(QUEUE_IS_ACTIVE, if (isActive) 1 else 0)
        }
        db.update(TABLE_QUEUES, values, "$QUEUE_ID=?", arrayOf(id.toString()))
        db.close()
    }

    // Automatically deactivate all queues whose end_time has passed
    fun autoDeactivateExpiredQueues() {
        val db = this.writableDatabase
        val cursor = db.query(TABLE_QUEUES, null, "$QUEUE_IS_ACTIVE=1", null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(QUEUE_ID))
                    val endTime = it.getString(it.getColumnIndexOrThrow(QUEUE_END_TIME))

                    if (hasTimePassed(endTime)) {
                        val values = ContentValues().apply {
                            put(QUEUE_IS_ACTIVE, 0)
                        }
                        db.update(TABLE_QUEUES, values, "$QUEUE_ID=?", arrayOf(id.toString()))
                    }
                } while (it.moveToNext())
            }
        }
        db.close()
    }

    // Check if time has passed
    private fun hasTimePassed(endTime: String): Boolean {
        return try {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val now = timeFormat.parse(timeFormat.format(Date()))
            val end = timeFormat.parse(endTime)
            now?.after(end) ?: false
        } catch (e: ParseException) {
            Log.e("TIME_PARSE", "Invalid time format: $endTime")
            false
        }
    }

    // Basic login check
    fun checkLogin(identifier: String, password: String, userType: String): Boolean {
        val db = this.readableDatabase
        val hashedPassword = hashPassword(password)
        var cursor: Cursor? = null
        var loginSuccess = false

        cursor = when (userType) {
            "operator" -> db.query(TABLE_OPERATORS, arrayOf(OPERATOR_ID), "$OPERATOR_PHONE=? AND $OPERATOR_PASSWORD=?", arrayOf(identifier, hashedPassword), null, null, null)
            "driver" -> db.query(TABLE_DRIVERS, arrayOf(DRIVER_ID), "$DRIVER_PHONE=? AND $DRIVER_PASSWORD=?", arrayOf(identifier, hashedPassword), null, null, null)
            "passenger" -> db.query(TABLE_PASSENGERS, arrayOf(PASSENGER_ID), "$PASSENGER_FULL_NAME=? AND $PASSENGER_PASSWORD=?", arrayOf(identifier, hashedPassword), null, null, null)
            else -> null
        }

        if (cursor != null && cursor.count > 0) {
            loginSuccess = true
        }
        cursor?.close()
        db.close()
        return loginSuccess
    }

    private fun hashPassword(password: String): String {
        return password // Simple placeholder
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}