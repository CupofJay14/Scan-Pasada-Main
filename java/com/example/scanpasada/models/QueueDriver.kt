package com.example.scanpasada.models

import java.util.Date

data class QueueDriver(
    var id: String? = null,
    var queueId: String? = null,
    var driverId: String? = null,
    var driverName: String? = null,
    var plateNumber: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var joinedAt: Date? = null,
    var isActive: Boolean = true
) {
    constructor(queueId: String, driverId: String, driverName: String, plateNumber: String) : this(
        queueId = queueId,
        driverId = driverId,
        driverName = driverName,
        plateNumber = plateNumber,
        joinedAt = Date(),
        isActive = true
    )
}
