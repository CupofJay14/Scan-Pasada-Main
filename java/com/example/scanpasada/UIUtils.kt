package com.example.scanpasada

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

object UIUtils {
    
    // Show success toast with green color
    fun showSuccessToast(context: Context, message: String) {
        val toast = Toast.makeText(context, "✅ $message", Toast.LENGTH_SHORT)
        val view = toast.view
        if (view != null) {
            view.setBackgroundColor(Color.parseColor("#4CAF50"))
            val textView = view.findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(Color.WHITE)
        }
        toast.show()
    }
    
    // Show error toast with red color
    fun showErrorToast(context: Context, message: String) {
        val toast = Toast.makeText(context, "❌ $message", Toast.LENGTH_LONG)
        val view = toast.view
        if (view != null) {
            view.setBackgroundColor(Color.parseColor("#F44336"))
            val textView = view.findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(Color.WHITE)
        }
        toast.show()
    }
    
    // Show info toast with blue color
    fun showInfoToast(context: Context, message: String) {
        val toast = Toast.makeText(context, "ℹ️ $message", Toast.LENGTH_SHORT)
        val view = toast.view
        if (view != null) {
            view.setBackgroundColor(Color.parseColor("#2196F3"))
            val textView = view.findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(Color.WHITE)
        }
        toast.show()
    }
    
    // Show warning toast with orange color
    fun showWarningToast(context: Context, message: String) {
        val toast = Toast.makeText(context, "⚠️ $message", Toast.LENGTH_LONG)
        val view = toast.view
        if (view != null) {
            view.setBackgroundColor(Color.parseColor("#FF9800"))
            val textView = view.findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(Color.WHITE)
        }
        toast.show()
    }
    
    // Fade in animation
    fun fadeIn(view: View, duration: Long) {
        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = duration
        fadeIn.fillAfter = true
        view.startAnimation(fadeIn)
    }
    
    // Fade out animation
    fun fadeOut(view: View, duration: Long) {
        val fadeOut = AlphaAnimation(1.0f, 0.0f)
        fadeOut.duration = duration
        fadeOut.fillAfter = true
        view.startAnimation(fadeOut)
    }
    
    // Slide in from right animation
    fun slideInFromRight(view: View, duration: Long) {
        val slideIn = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f
        )
        slideIn.duration = duration
        slideIn.fillAfter = true
        view.startAnimation(slideIn)
    }
    
    // Slide out to left animation
    fun slideOutToLeft(view: View, duration: Long) {
        val slideOut = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f
        )
        slideOut.duration = duration
        slideOut.fillAfter = true
        view.startAnimation(slideOut)
    }
    
    // Create rounded button background
    fun createRoundedButtonBackground(backgroundColor: Int, cornerRadius: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            this.cornerRadius = cornerRadius.toFloat()
        }
    }
    
    // Create rounded button background with border
    fun createRoundedButtonWithBorder(backgroundColor: Int, borderColor: Int, cornerRadius: Int, borderWidth: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            this.cornerRadius = cornerRadius.toFloat()
            setStroke(borderWidth, borderColor)
        }
    }
    
    // Set button state (enabled/disabled)
    fun setButtonState(button: View, enabled: Boolean) {
        button.isEnabled = enabled
        button.alpha = if (enabled) 1.0f else 0.5f
    }
    
    // Show loading state
    fun showLoadingState(textView: TextView, loadingText: String) {
        textView.text = loadingText
        textView.isEnabled = false
        textView.alpha = 0.7f
    }
    
    // Hide loading state
    fun hideLoadingState(textView: TextView, originalText: String) {
        textView.text = originalText
        textView.isEnabled = true
        textView.alpha = 1.0f
    }
    
    // Create status indicator
    fun setStatusIndicator(statusView: TextView, isActive: Boolean, activeText: String, inactiveText: String) {
        if (isActive) {
            statusView.text = "🟢 $activeText"
            statusView.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            statusView.text = "🔴 $inactiveText"
            statusView.setTextColor(Color.parseColor("#F44336"))
        }
    }
    
    // Create location status indicator
    fun setLocationStatus(locationView: TextView, isTracking: Boolean) {
        if (isTracking) {
            locationView.text = "📍 Location sharing active"
            locationView.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            locationView.text = "📍 Location sharing inactive"
            locationView.setTextColor(Color.parseColor("#757575"))
        }
    }
    
    // Create queue status indicator
    fun setQueueStatus(queueView: TextView, isActive: Boolean, driverCount: Int) {
        if (isActive) {
            queueView.text = "🟢 Active Queue ($driverCount drivers)"
            queueView.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            queueView.text = "🔴 Inactive Queue"
            queueView.setTextColor(Color.parseColor("#F44336"))
        }
    }
    
    // Create driver status indicator
    fun setDriverStatus(driverView: TextView, isOnline: Boolean, driverName: String) {
        if (isOnline) {
            driverView.text = "🟢 $driverName (Online)"
            driverView.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            driverView.text = "🔴 $driverName (Offline)"
            driverView.setTextColor(Color.parseColor("#F44336"))
        }
    }
    
    // Format time display
    fun formatTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }
    
    // Format date display
    fun formatDate(year: Int, month: Int, day: Int): String {
        return String.format("%04d-%02d-%02d", year, month + 1, day)
    }
    
    // Format distance display
    fun formatDistance(distanceInMeters: Double): String {
        return if (distanceInMeters < 1000) {
            String.format("%.0f m", distanceInMeters)
        } else {
            String.format("%.1f km", distanceInMeters / 1000)
        }
    }
    
    // Create progress indicator text
    fun createProgressText(current: Int, total: Int): String {
        return String.format("Progress: %d/%d", current, total)
    }
    
    // Create percentage text
    fun createPercentageText(percentage: Double): String {
        return String.format("%.1f%%", percentage)
    }
    
    // Validate and format phone number
    fun formatPhoneNumber(phoneNumber: String?): String {
        if (phoneNumber.isNullOrEmpty()) {
            return ""
        }
        
        // Remove all non-digit characters
        val digits = phoneNumber.replace(Regex("[^0-9]"), "")
        
        // Format Philippine phone number
        if (digits.length == 11 && digits.startsWith("09")) {
            return "${digits.substring(0, 4)} ${digits.substring(4, 7)} ${digits.substring(7)}"
        }
        
        return phoneNumber
    }
    
    // Validate and format plate number
    fun formatPlateNumber(plateNumber: String?): String {
        if (plateNumber.isNullOrEmpty()) {
            return ""
        }
        
        // Convert to uppercase and format
        val formatted = plateNumber.uppercase().replace(Regex("[^A-Z0-9]"), "")
        
        if (formatted.length >= 6) {
            return "${formatted.substring(0, 3)}-${formatted.substring(3)}"
        }
        
        return plateNumber
    }
}
