package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class DriverSignUpFormPage : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLicenseNumber: EditText
    private lateinit var etPlateNumber: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvBackButton: TextView
    private lateinit var authManager: SimpleAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_signup_form)

        authManager = SimpleAuthManager(this)
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etPhone = findViewById(R.id.etPhone)
        etLicenseNumber = findViewById(R.id.etLicenseNumber)
        etPlateNumber = findViewById(R.id.etPlateNumber)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvBackButton = findViewById(R.id.tvBackButton)
    }

    private fun setupClickListeners() {
        tvBackButton.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            if (validateInput()) {
                performSignUp()
            }
        }
    }

    private fun validateInput(): Boolean {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val licenseNumber = etLicenseNumber.text.toString().trim()
        val plateNumber = etPlateNumber.text.toString().trim()

        if (fullName.isEmpty()) {
            etFullName.error = "Full name is required"
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email"
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return false
        }

        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            return false
        }

        if (licenseNumber.isEmpty()) {
            etLicenseNumber.error = "License number is required"
            return false
        }

        if (plateNumber.isEmpty()) {
            etPlateNumber.error = "Plate number is required"
            return false
        }

        return true
    }

    private fun performSignUp() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val licenseNumber = etLicenseNumber.text.toString().trim()
        val plateNumber = etPlateNumber.text.toString().trim()

        // Show loading
        btnSignUp.isEnabled = false
        btnSignUp.text = "Creating Account..."

        // Prepare user data
        val userData = mapOf(
            "fullName" to fullName,
            "phone" to phone,
            "licenseNumber" to licenseNumber,
            "plateNumber" to plateNumber
        )

        // Perform sign up in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = authManager.signUp(email, password, "driver", userData)

                withContext(Dispatchers.Main) {
                    btnSignUp.isEnabled = true
                    btnSignUp.text = "Sign Up"

                    if (success) {
                        Toast.makeText(this@DriverSignUpFormPage, "Account created successfully!", Toast.LENGTH_LONG).show()
                        
                        // Navigate to driver login page
                        val intent = Intent(this@DriverSignUpFormPage, DriverLoginPage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = authManager.lastErrorMessage ?: "Sign up failed. Please try again."
                        Toast.makeText(this@DriverSignUpFormPage, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnSignUp.isEnabled = true
                    btnSignUp.text = "Sign Up"
                    Toast.makeText(this@DriverSignUpFormPage, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
