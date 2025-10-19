package com.example.scanpasada.models

data class User(
    var id: String? = null,
    var email: String? = null,
    var password: String? = null,
    var userType: String? = null, // "operator", "driver", "passenger"
    var fullName: String? = null,
    var phoneNumber: String? = null,
    var plateNumber: String? = null, // For drivers only
    var licenseNumber: String? = null, // For drivers only
    var expiryDate: String? = null, // For drivers only
    var terminalName: String? = null, // For operators only
    var terminalLocation: String? = null, // For operators only
    var age: Int = 0, // For passengers only
    var address: String? = null, // For passengers only
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var isOnline: Boolean = false,
    var isActive: Boolean = true
) {
    constructor(id: String, email: String, password: String, userType: String, fullName: String, phoneNumber: String) : this(
        id = id,
        email = email,
        password = password,
        userType = userType,
        fullName = fullName,
        phoneNumber = phoneNumber,
        isActive = true,
        isOnline = false
    )
}
