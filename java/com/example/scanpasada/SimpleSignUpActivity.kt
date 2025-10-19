package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SimpleSignUpActivity : AppCompatActivity() {
    
    private lateinit var authManager: SimpleAuthManager
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLicenseNumber: EditText
    private lateinit var etPlateNumber: EditText
    private lateinit var btnSignUp: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("SimpleSignUpActivity", "onCreate called")
        setContentView(R.layout.activity_driver_signup_form) // Using existing layout
        
        authManager = SimpleAuthManager(this)
        initializeViews()
        setupListeners()
        
        android.util.Log.d("SimpleSignUpActivity", "Activity initialized successfully")
    }
    
    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etLicenseNumber = findViewById(R.id.etLicenseNumber)
        etPlateNumber = findViewById(R.id.etPlateNumber)
        btnSignUp = findViewById(R.id.btnSignUp)
    }
    
    private fun setupListeners() {
        btnSignUp.setOnClickListener {
            performSignUp()
        }
        
        // Back button functionality
        findViewById<TextView>(R.id.tvBackButton).setOnClickListener {
            finish()
        }
    }
    
    private fun performSignUp() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val licenseNumber = etLicenseNumber.text.toString().trim()
        val plateNumber = etPlateNumber.text.toString().trim()
        
        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            fullName.isEmpty() || phone.isEmpty() || licenseNumber.isEmpty() || plateNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Default to driver
        val selectedUserType = "driver"
        
        // Prepare user data for driver
        val userData = mapOf(
            "fullName" to fullName,
            "phone" to phone,
            "licenseNumber" to licenseNumber,
            "plateNumber" to plateNumber,
            "expiryDate" to "2099-12-31", // Default value
            "latitude" to 0.0, // Default value
            "longitude" to 0.0, // Default value
            "isOnline" to false // Default value
        )
        
        // Show loading
        btnSignUp.isEnabled = false
        btnSignUp.text = "Creating Account..."
        
        // Perform sign up in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = authManager.signUp(email, password, selectedUserType, userData)
                
                withContext(Dispatchers.Main) {
                    btnSignUp.isEnabled = true
                    btnSignUp.text = "Sign Up"
                    
                    if (success) {
                        Toast.makeText(this@SimpleSignUpActivity, "Account created! Please check your email for confirmation.", Toast.LENGTH_LONG).show()
                        
                        // Navigate to email confirmation page
                        val intent = Intent(this@SimpleSignUpActivity, EmailConfirmationActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)
                        intent.putExtra("userType", selectedUserType)
                        intent.putExtra("userData", userData as HashMap<String, Any>)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = authManager.lastErrorMessage ?: "Sign up failed. Please try again."
                        Toast.makeText(this@SimpleSignUpActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnSignUp.isEnabled = true
                    btnSignUp.text = "Sign Up"
                    Toast.makeText(this@SimpleSignUpActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
