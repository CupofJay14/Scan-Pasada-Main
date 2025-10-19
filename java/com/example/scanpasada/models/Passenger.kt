package com.example.scanpasada.models

// This class must match the column names in your Supabase 'passengers' table.
data class Passenger(
    val user_id: String,
    val full_name: String,
    val age: Int,
    val address: String
)
