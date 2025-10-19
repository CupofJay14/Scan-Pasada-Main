package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SimpleLoginActivity : AppCompatActivity() {
    
    private lateinit var authManager: SimpleAuthManager
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("SimpleLoginActivity", "onCreate called")
        setContentView(R.layout.activity_driver_login_page) // Using existing layout
        
        authManager = SimpleAuthManager(this)
        initializeViews()
        setupListeners()
        
        android.util.Log.d("SimpleLoginActivity", "Activity initialized successfully")
        
        // Try to load existing user
        if (authManager.loadCurrentUser()) {
            android.util.Log.d("SimpleLoginActivity", "User already logged in, navigating to home")
            navigateToHome()
        }
    }
    
    private fun initializeViews() {
        etEmail = findViewById(R.id.etDriverEmailLI)
        etPassword = findViewById(R.id.etDriverPasswordLi)
        btnLogin = findViewById(R.id.tvDriverLoginButton)
    }
    
    private fun setupListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }
        
        // Back button functionality
        findViewById<TextView>(R.id.tvBackButton7).setOnClickListener {
            finish()
        }
    }
    
    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }
        
        // For now, default to driver - you can modify this based on your needs
        val selectedUserType = "driver"
        
        // Show loading
        btnLogin.isEnabled = false
        btnLogin.text = "Signing In..."
        
        // Perform login in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = authManager.login(email, password, selectedUserType)
                
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"
                    
                    if (success) {
                        Toast.makeText(this@SimpleLoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    } else {
                        val errorMessage = authManager.lastErrorMessage ?: "Login failed. Please try again."
                        Toast.makeText(this@SimpleLoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"
                    Toast.makeText(this@SimpleLoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun navigateToHome() {
        val user = authManager.currentUser
        if (user != null) {
            val intent = when (user.userType) {
                "driver" -> Intent(this, DriverHomePage::class.java)
                "passenger" -> Intent(this, PassengerHomePage::class.java)
                "operator" -> Intent(this, OperatorHomePage::class.java)
                else -> Intent(this, MainActivity2::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}
