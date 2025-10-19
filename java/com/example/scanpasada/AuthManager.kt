package com.example.scanpasada

import android.content.Context
import android.content.SharedPreferences
import com.example.scanpasada.models.User
import com.google.gson.Gson
import io.github.jan.supabase.gotrue.user.UserInfo

class AuthManager private constructor(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "ScanPasadaAuth"
        private const val KEY_USER = "current_user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_PENDING_EMAIL = "pending_email"
        private const val KEY_PENDING_PASSWORD = "pending_password"
        private const val KEY_PENDING_USERTYPE = "pending_user_type"
        
        @Volatile
        private var INSTANCE: AuthManager? = null
        
        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var currentUser: User? = null
    private var lastErrorMessage: String? = null
    
    init {
        loadCurrentUser()
    }
    
    fun login(email: String, password: String, userType: String): Boolean {
        // Input validation
        if (!ValidationUtils.isValidEmail(email)) {
            return false
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            return false
        }
        
        if (userType !in listOf("operator", "driver", "passenger")) {
            return false
        }
        
        return try {
            val service = SupabaseService()
            val info = service.signIn(email, password)
            
            // Check if user exists in the appropriate database table
            android.util.Log.d("AuthManager", "Checking if user exists in database table for userType: $userType")
            val userExistsInTable = service.checkUserExistsInTable(info.id, userType)
            android.util.Log.d("AuthManager", "User exists in table: $userExistsInTable")
            
            if (!userExistsInTable) {
                lastErrorMessage = "User account not found. Please complete your registration."
                android.util.Log.e("AuthManager", "User not found in database table")
                return false
            }
            
            currentUser = User(
                id = info.id,
                email = email,
                userType = userType,
                fullName = service.getUserNameFromTable(info.id, userType),
                isActive = true
            )
            
            saveCurrentUser()
            lastErrorMessage = null
            true
        } catch (e: Exception) {
            lastErrorMessage = e.message
            false
        }
    }
    
    fun register(email: String, password: String, userType: String, name: String, phoneNumber: String): Boolean {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            return false
        }
        
        return try {
            val service = SupabaseService()
            val info = service.signUp(email, password)
            
            currentUser = User(
                id = info.id,
                email = email,
                userType = userType,
                fullName = name,
                phoneNumber = phoneNumber,
                isActive = true
            )
            
            saveCurrentUser()
            lastErrorMessage = null
            true
        } catch (e: Exception) {
            lastErrorMessage = e.message
            false
        }
    }
    
    fun registerWithoutLogin(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) return false
        return try {
            val service = SupabaseService()
            service.signUpOnly(email, password)
            lastErrorMessage = null
            true
        } catch (e: Exception) {
            lastErrorMessage = e.message
            false
        }
    }
    
    fun logout() {
        currentUser = null
        prefs.edit().remove(KEY_USER).remove(KEY_IS_LOGGED_IN).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return currentUser != null && prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getCurrentUser(): User? = currentUser
    
    fun getUserType(): String? = currentUser?.userType
    
    fun getLastErrorMessage(): String? = lastErrorMessage
    
    fun setPendingCredentials(email: String, password: String, userType: String) {
        prefs.edit()
            .putString(KEY_PENDING_EMAIL, email)
            .putString(KEY_PENDING_PASSWORD, password)
            .putString(KEY_PENDING_USERTYPE, userType)
            .apply()
    }
    
    fun getPendingEmail(): String? = prefs.getString(KEY_PENDING_EMAIL, null)
    
    fun getPendingPassword(): String? = prefs.getString(KEY_PENDING_PASSWORD, null)
    
    fun getPendingUserType(): String? = prefs.getString(KEY_PENDING_USERTYPE, null)
    
    fun clearPendingCredentials() {
        prefs.edit()
            .remove(KEY_PENDING_EMAIL)
            .remove(KEY_PENDING_PASSWORD)
            .remove(KEY_PENDING_USERTYPE)
            .apply()
    }
    
    private fun saveCurrentUser() {
        currentUser?.let { user ->
            val gson = Gson()
            val userJson = gson.toJson(user)
            prefs.edit()
                .putString(KEY_USER, userJson)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply()
        }
    }
    
    private fun loadCurrentUser() {
        if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            val userJson = prefs.getString(KEY_USER, null)
            if (userJson != null) {
                val gson = Gson()
                currentUser = gson.fromJson(userJson, User::class.java)
            }
        }
    }
    
    private fun getUserNameByEmail(email: String): String {
        return when (email) {
            "operator@scanpasada.com" -> "Terminal Operator"
            "driver@scanpasada.com" -> "Jeepney Driver"
            "passenger@scanpasada.com" -> "Regular Passenger"
            else -> "User"
        }
    }
}
