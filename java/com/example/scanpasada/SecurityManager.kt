package com.example.scanpasada

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SecurityManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "SecurityManager"
        @Volatile
        private var INSTANCE: SecurityManager? = null
        
        fun getInstance(context: Context): SecurityManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecurityManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Required permissions for the app
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    
    // Dangerous permissions that need runtime request
    private val DANGEROUS_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    
    // Check if all required permissions are granted
    fun hasAllPermissions(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Missing permission: $permission")
                return false
            }
        }
        return true
    }
    
    // Check if dangerous permissions are granted
    fun hasDangerousPermissions(): Boolean {
        for (permission in DANGEROUS_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Missing dangerous permission: $permission")
                return false
            }
        }
        return true
    }
    
    // Get list of missing permissions
    fun getMissingPermissions(): List<String> {
        val missingPermissions = mutableListOf<String>()
        for (permission in REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        return missingPermissions
    }
    
    // Get list of missing dangerous permissions
    fun getMissingDangerousPermissions(): List<String> {
        val missingPermissions = mutableListOf<String>()
        for (permission in DANGEROUS_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        return missingPermissions
    }
    
    // Hash password for secure storage
    fun hashPassword(password: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(password.toByteArray())
            val hexString = StringBuilder()
            
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            
            hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Error hashing password", e)
            password // Fallback to plain text (not recommended for production)
        }
    }
    
    // Validate device security
    fun isDeviceSecure(): Boolean {
        // Check if device is rooted (basic check)
        if (isRooted()) {
            Log.w(TAG, "Device appears to be rooted")
            return false
        }
        
        // Check if debugging is enabled
        if (isDebuggingEnabled()) {
            Log.w(TAG, "Debugging is enabled")
            return false
        }
        
        return true
    }
    
    // Check if device is rooted
    private fun isRooted(): Boolean {
        val rootPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        
        for (path in rootPaths) {
            if (File(path).exists()) {
                return true
            }
        }
        
        return false
    }
    
    // Check if debugging is enabled
    private fun isDebuggingEnabled(): Boolean {
        return (context.applicationInfo.flags and 
                android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    
    // Validate app integrity
    fun isAppIntegrityValid(): Boolean {
        // Check if app signature is valid
        return try {
            val packageInfo = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            
            val signatures = packageInfo.signatures
            if (signatures == null || signatures.isEmpty()) {
                Log.w(TAG, "No signatures found")
                false
            } else {
                // In production, you would verify the signature against a known good signature
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking app integrity", e)
            false
        }
    }
    
    // Sanitize user input
    fun sanitizeInput(input: String?): String {
        if (input.isNullOrEmpty()) {
            return ""
        }
        
        // Remove potentially dangerous characters
        return input.replace(Regex("[<>\"'&]"), "")
                   .trim()
    }
    
    // Validate QR code content
    fun isValidQRContent(qrContent: String?): Boolean {
        if (qrContent.isNullOrEmpty()) {
            return false
        }
        
        // Check for malicious content
        val dangerousPatterns = arrayOf(
            "javascript:",
            "data:",
            "vbscript:",
            "onload=",
            "onerror=",
            "<script",
            "eval(",
            "function("
        )
        
        val lowerContent = qrContent.lowercase()
        for (pattern in dangerousPatterns) {
            if (lowerContent.contains(pattern)) {
                Log.w(TAG, "Potentially malicious QR content detected: $pattern")
                return false
            }
        }
        
        return true
    }
    
    // Get security status
    fun getSecurityStatus(): SecurityStatus {
        return SecurityStatus(
            hasAllPermissions = hasAllPermissions(),
            hasDangerousPermissions = hasDangerousPermissions(),
            isDeviceSecure = isDeviceSecure(),
            isAppIntegrityValid = isAppIntegrityValid(),
            missingPermissions = getMissingPermissions(),
            missingDangerousPermissions = getMissingDangerousPermissions()
        )
    }
    
    // Security status data class
    data class SecurityStatus(
        val hasAllPermissions: Boolean,
        val hasDangerousPermissions: Boolean,
        val isDeviceSecure: Boolean,
        val isAppIntegrityValid: Boolean,
        val missingPermissions: List<String>,
        val missingDangerousPermissions: List<String>
    ) {
        fun isSecure(): Boolean {
            return hasAllPermissions && isDeviceSecure && isAppIntegrityValid
        }
    }
}
