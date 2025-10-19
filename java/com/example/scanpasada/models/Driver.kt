package com.example.scanpasada.models

// This class must match the column names in your Supabase 'drivers' table.
data class Driver(
    val user_id: String,
    val full_name: String,
    val phone: String,
    val license_number: String,
    val expiry_date: String, // Using "YYYY-MM-DD" format
    val plate_number: String
)
