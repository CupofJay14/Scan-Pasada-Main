package com.example.scanpasada.models

// This class represents the data structure for the 'operators' table.
// Column names here must exactly match your Supabase table.
data class Operator(
    val user_id: String,
    val full_name: String,
    val phone: String,
    val terminal_name: String,
    val terminal_location: String
)
