package com.example.scanpasada

data class QueueItem(
    val id: Int,
    val route: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean
)