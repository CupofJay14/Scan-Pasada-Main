package com.example.scanpasada

import android.content.Context
import com.example.scanpasada.models.User

class RoleManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: RoleManager? = null
        
        fun getInstance(context: Context): RoleManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RoleManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val authManager = AuthManager.getInstance(context)
    
    /**
     * Check if current user is an operator
     */
    fun isOperator(): Boolean {
        val currentUser = authManager.getCurrentUser()
        return currentUser != null && "operator" == currentUser.userType
    }
    
    /**
     * Check if current user is a driver
     */
    fun isDriver(): Boolean {
        val currentUser = authManager.getCurrentUser()
        return currentUser != null && "driver" == currentUser.userType
    }
    
    /**
     * Check if current user is a passenger
     */
    fun isPassenger(): Boolean {
        val currentUser = authManager.getCurrentUser()
        return currentUser != null && "passenger" == currentUser.userType
    }
    
    /**
     * Check if user can manage queues (create, edit, delete, end)
     */
    fun canManageQueues(): Boolean {
        return isOperator()
    }
    
    /**
     * Check if user can join queues
     */
    fun canJoinQueues(): Boolean {
        return isDriver()
    }
    
    /**
     * Check if user can view queues
     */
    fun canViewQueues(): Boolean {
        return isOperator() || isDriver() || isPassenger()
    }
    
    /**
     * Check if user can manage schedules
     */
    fun canManageSchedules(): Boolean {
        return isOperator()
    }
    
    /**
     * Check if user can view schedules
     */
    fun canViewSchedules(): Boolean {
        return isOperator() || isDriver() || isPassenger()
    }
    
    /**
     * Check if user can monitor drivers
     */
    fun canMonitorDrivers(): Boolean {
        return isOperator()
    }
    
    /**
     * Check if user can track drivers (view driver locations)
     */
    fun canTrackDrivers(): Boolean {
        return isOperator() || isPassenger()
    }
    
    /**
     * Check if user can share location
     */
    fun canShareLocation(): Boolean {
        return isDriver()
    }
    
    /**
     * Get current user's role for display purposes
     */
    fun getCurrentUserRole(): String {
        val currentUser = authManager.getCurrentUser()
        if (currentUser == null) return "Unknown"
        
        return when (currentUser.userType) {
            "operator" -> "Operator"
            "driver" -> "Driver"
            "passenger" -> "Passenger"
            else -> "Unknown"
        }
    }
}
