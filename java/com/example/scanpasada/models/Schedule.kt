package com.example.scanpasada.models

import java.util.Date

data class Schedule(
    var title: String,
    var description: String,
    var date: Date,
    var startTime: String,
    var endTime: String,
    var operatorId: Long? = null,
    var id: Long? = null,
    var isActive: Boolean = true,
    var createdAt: Date? = null
)
