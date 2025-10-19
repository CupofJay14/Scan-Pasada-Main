package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PassengerSignUpForm : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etAge: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvBackButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_signup_form)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etAge = findViewById(R.id.etAge)
        etAddress = findViewById(R.id.etAddress)
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
                goToWaitingScreen()
            }
        }
    }

    private fun validateInput(): Boolean {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val age = etAge.text.toString().trim()
        val address = etAddress.text.toString().trim()

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

        if (age.isEmpty()) {
            etAge.error = "Age is required"
            return false
        }

        try {
            val ageInt = age.toInt()
            if (ageInt < 1 || ageInt > 120) {
                etAge.error = "Please enter a valid age"
                return false
            }
        } catch (e: NumberFormatException) {
            etAge.error = "Please enter a valid number"
            return false
        }

        if (address.isEmpty()) {
            etAddress.error = "Address is required"
            return false
        }

        return true
    }

    private fun goToWaitingScreen() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val age = etAge.text.toString().trim()
        val address = etAddress.text.toString().trim()
        
        AuthManager.getInstance(this).setPendingCredentials(email, password, "passenger")
        val intent = Intent(this, EmailConfirmationActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        intent.putExtra("userType", "passenger")
        intent.putExtra("fullName", fullName)
        intent.putExtra("age", age.toInt())
        intent.putExtra("address", address)
        startActivity(intent)
        finish()
    }
}
