package com.example.scanpasada

import android.content.Context
import android.content.SharedPreferences
import com.example.scanpasada.models.User

class SimpleAuthManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("simple_auth", Context.MODE_PRIVATE)
    private val authService = SimpleAuthService()
    
    var currentUser: User? = null
        private set
    
    var lastErrorMessage: String? = null
        private set

    // Simple sign up - just create user in Supabase Auth (database insertion happens after email confirmation)
    fun signUp(email: String, password: String, userType: String, userData: Map<String, Any>): Boolean {
        android.util.Log.d("SimpleAuthManager", "Starting sign up for: $email, type: $userType")
        
        // Step 1: Create user in Supabase Auth
        val signUpSuccess = authService.signUp(email, password)
        if (!signUpSuccess) {
            lastErrorMessage = "Failed to create account. Please try again."
            return false
        }
        
        android.util.Log.d("SimpleAuthManager", "User created in Supabase Auth successfully")
        android.util.Log.d("SimpleAuthManager", "Email confirmation required - database insertion will happen after confirmation")
        return true
    }
    
    // Complete signup after email confirmation - insert data and login
    fun completeSignUp(email: String, password: String, userType: String, userData: Map<String, Any>): Boolean {
        android.util.Log.d("SimpleAuthManager", "Completing sign up for: $email, type: $userType")
        
        // Step 1: Sign in to get user ID (after email confirmation)
        val userInfo = authService.signIn(email, password)
        if (userInfo == null) {
            lastErrorMessage = "Email confirmed but failed to sign in. Please try logging in manually."
            return false
        }
        
        android.util.Log.d("SimpleAuthManager", "Got user ID after confirmation: ${userInfo.id}")
        
        // Step 2: Insert user data into appropriate table
        android.util.Log.d("SimpleAuthManager", "=== INSERTING USER DATA AFTER CONFIRMATION ===")
        android.util.Log.d("SimpleAuthManager", "User ID: ${userInfo.id}")
        android.util.Log.d("SimpleAuthManager", "User Type: $userType")
        android.util.Log.d("SimpleAuthManager", "User Data: $userData")
        
        val insertSuccess = when (userType) {
            "driver" -> {
                val fullName = userData["fullName"] as? String ?: ""
                val phone = userData["phone"] as? String ?: ""
                val licenseNumber = userData["licenseNumber"] as? String ?: ""
                val plateNumber = userData["plateNumber"] as? String ?: ""
                android.util.Log.d("SimpleAuthManager", "Inserting driver with data: name=$fullName, phone=$phone, license=$licenseNumber, plate=$plateNumber")
                val result = authService.insertDriver(userInfo.id, fullName, phone, licenseNumber, plateNumber)
                android.util.Log.d("SimpleAuthManager", "Driver insert result: $result")
                result
            }
            "passenger" -> {
                val fullName = userData["fullName"] as? String ?: ""
                val age = userData["age"] as? Int ?: 0
                val address = userData["address"] as? String ?: ""
                android.util.Log.d("SimpleAuthManager", "Inserting passenger with data: name=$fullName, age=$age, address=$address")
                val result = authService.insertPassenger(userInfo.id, fullName, age, address)
                android.util.Log.d("SimpleAuthManager", "Passenger insert result: $result")
                result
            }
            "operator" -> {
                val fullName = userData["fullName"] as? String ?: ""
                val phone = userData["phone"] as? String ?: ""
                val terminalName = userData["terminalName"] as? String ?: ""
                val terminalLocation = userData["terminalLocation"] as? String ?: ""
                android.util.Log.d("SimpleAuthManager", "Inserting operator with data: name=$fullName, phone=$phone, terminal=$terminalName, location=$terminalLocation")
                val result = authService.insertOperator(userInfo.id, fullName, phone, terminalName, terminalLocation)
                android.util.Log.d("SimpleAuthManager", "Operator insert result: $result")
                result
            }
            else -> {
                android.util.Log.e("SimpleAuthManager", "Invalid user type: $userType")
                false
            }
        }
        
        if (!insertSuccess) {
            lastErrorMessage = "Email confirmed but failed to save profile. Please check the logs for details."
            android.util.Log.e("SimpleAuthManager", "Database insertion failed for user: ${userInfo.id}")
            return false
        }
        
        android.util.Log.d("SimpleAuthManager", "User data inserted successfully")
        
        // Step 3: Set current user
        currentUser = User(
            id = userInfo.id,
            email = email,
            userType = userType,
            fullName = userData["fullName"] as? String ?: "User"
        )
        
        // Step 4: Save user session
        saveCurrentUser()
        
        android.util.Log.d("SimpleAuthManager", "Sign up completed successfully")
        return true
    }

    // Simple login
    fun login(email: String, password: String, userType: String): Boolean {
        android.util.Log.d("SimpleAuthManager", "Starting login for: $email, type: $userType")
        
        // Step 1: Sign in to Supabase
        val userInfo = authService.signIn(email, password)
        if (userInfo == null) {
            lastErrorMessage = "Invalid email or password."
            return false
        }
        
        android.util.Log.d("SimpleAuthManager", "Supabase login successful, checking user in table")
        
        // Step 2: Check if user exists in appropriate table
        val userExists = authService.checkUserInTable(userInfo.id, userType)
        if (!userExists) {
            lastErrorMessage = "User profile not found. Please complete your registration."
            return false
        }
        
        android.util.Log.d("SimpleAuthManager", "User found in table, getting user name")
        
        // Step 3: Get user name and set current user
        val fullName = authService.getUserName(userInfo.id, userType)
        currentUser = User(
            id = userInfo.id,
            email = email,
            userType = userType,
            fullName = fullName
        )
        
        saveCurrentUser()
        lastErrorMessage = null
        return true
    }

    // Save current user to preferences
    private fun saveCurrentUser() {
        currentUser?.let { user ->
            prefs.edit().apply {
                putString("user_id", user.id)
                putString("email", user.email)
                putString("user_type", user.userType)
                putString("full_name", user.fullName)
                putBoolean("is_active", user.isActive)
                apply()
            }
            android.util.Log.d("SimpleAuthManager", "User saved to preferences")
        }
    }

    // Load current user from preferences
    fun loadCurrentUser(): Boolean {
        val userId = prefs.getString("user_id", null)
        val email = prefs.getString("email", null)
        val userType = prefs.getString("user_type", null)
        val fullName = prefs.getString("full_name", null)
        val isActive = prefs.getBoolean("is_active", false)
        
        if (userId != null && email != null && userType != null && fullName != null) {
            currentUser = User(
                id = userId,
                email = email,
                userType = userType,
                fullName = fullName
            )
            android.util.Log.d("SimpleAuthManager", "User loaded from preferences: $fullName")
            return true
        }
        
        android.util.Log.d("SimpleAuthManager", "No saved user found")
        return false
    }

    // Get current user - using property access instead of function

    // Logout
    fun logout() {
        currentUser = null
        prefs.edit().clear().apply()
        android.util.Log.d("SimpleAuthManager", "User logged out")
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean = currentUser != null
}
