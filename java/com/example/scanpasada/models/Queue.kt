package com.example.scanpasada.models

import java.util.Date

data class Queue(
    var id: Long? = null,
    var route: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var isActive: Boolean = true,
    var operatorId: Long? = null,
    var qrCode: String? = null,
    var description: String? = null,
    var createdAt: Date? = null
) {
    constructor(route: String, startTime: String, endTime: String, operatorId: Long) : this(
        route = route,
        startTime = startTime,
        endTime = endTime,
        operatorId = operatorId,
        isActive = true,
        createdAt = Date()
    )
}
