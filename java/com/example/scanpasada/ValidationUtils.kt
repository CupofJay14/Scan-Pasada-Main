package com.example.scanpasada

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {
    
    // Email validation
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    // Password validation
    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrEmpty() || password.length < 6) {
            return false
        }
        // At least 6 characters
        return password.length >= 6
    }
    
    // Phone number validation (Philippines format)
    fun isValidPhoneNumber(phone: String?): Boolean {
        if (phone.isNullOrBlank()) {
            return false
        }
        // Remove spaces and special characters
        val cleanPhone = phone.replace(Regex("[\\s\\-()]"), "")
        // Check if it's a valid Philippine phone number (10-11 digits starting with 09)
        val phonePattern = Pattern.compile("^09[0-9]{9}$")
        return phonePattern.matcher(cleanPhone).matches()
    }
    
    // Plate number validation (Philippines format)
    fun isValidPlateNumber(plateNumber: String?): Boolean {
        if (plateNumber.isNullOrBlank()) {
            return false
        }
        // Philippine plate number format: ABC-1234 or ABC-123
        val platePattern = Pattern.compile("^[A-Z]{3}-[0-9]{3,4}$")
        return platePattern.matcher(plateNumber.uppercase()).matches()
    }
    
    // License number validation
    fun isValidLicenseNumber(licenseNumber: String?): Boolean {
        if (licenseNumber.isNullOrBlank()) {
            return false
        }
        // Basic license number validation (alphanumeric, 8-15 characters)
        return licenseNumber.length in 8..15
    }
    
    // Name validation
    fun isValidName(name: String?): Boolean {
        if (name.isNullOrBlank()) {
            return false
        }
        // At least 2 characters, only letters and spaces
        val namePattern = Pattern.compile("^[a-zA-Z\\s]{2,50}$")
        return namePattern.matcher(name.trim()).matches()
    }
    
    // Age validation
    fun isValidAge(age: Int): Boolean {
        return age in 18..100
    }
    
    // Route validation
    fun isValidRoute(route: String?): Boolean {
        if (route.isNullOrBlank()) {
            return false
        }
        return route.trim().length >= 3
    }
    
    // Time validation (HH:mm format)
    fun isValidTime(time: String?): Boolean {
        if (time.isNullOrBlank()) {
            return false
        }
        val timePattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
        return timePattern.matcher(time.trim()).matches()
    }
    
    // QR code validation
    fun isValidQRCode(qrCode: String?): Boolean {
        if (qrCode.isNullOrBlank()) {
            return false
        }
        // QR code should contain queue information
        return qrCode.contains("QUEUE_ID") && qrCode.length > 10
    }
    
    // Coordinate validation
    fun isValidLatitude(latitude: Double): Boolean {
        return latitude in -90.0..90.0
    }
    
    fun isValidLongitude(longitude: Double): Boolean {
        return longitude in -180.0..180.0
    }
    
    // Get validation error message
    fun getValidationErrorMessage(field: String, value: String): String? {
        return when (field.lowercase()) {
            "email" -> if (!isValidEmail(value)) "Please enter a valid email address" else null
            "password" -> if (!isValidPassword(value)) "Password must be at least 6 characters long" else null
            "phone" -> if (!isValidPhoneNumber(value)) "Please enter a valid Philippine phone number (09XXXXXXXXX)" else null
            "plate" -> if (!isValidPlateNumber(value)) "Please enter a valid plate number (ABC-1234)" else null
            "license" -> if (!isValidLicenseNumber(value)) "License number must be 8-15 characters long" else null
            "name" -> if (!isValidName(value)) "Name must be 2-50 characters and contain only letters" else null
            "route" -> if (!isValidRoute(value)) "Route must be at least 3 characters long" else null
            "time" -> if (!isValidTime(value)) "Please enter time in HH:mm format" else null
            else -> null
        }
    }
}