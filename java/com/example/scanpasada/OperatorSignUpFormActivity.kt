package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OperatorSignUpFormActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var etTerminalName: EditText
    private lateinit var etTerminalLocation: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvBackButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_signup_form)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etPhone = findViewById(R.id.etPhone)
        etTerminalName = findViewById(R.id.etTerminalName)
        etTerminalLocation = findViewById(R.id.etTerminalLocation)
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
        val phone = etPhone.text.toString().trim()
        val terminalName = etTerminalName.text.toString().trim()
        val terminalLocation = etTerminalLocation.text.toString().trim()

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

        if (terminalName.isEmpty()) {
            etTerminalName.error = "Terminal name is required"
            return false
        }

        if (terminalLocation.isEmpty()) {
            etTerminalLocation.error = "Terminal location is required"
            return false
        }

        return true
    }

    private fun goToWaitingScreen() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val terminalName = etTerminalName.text.toString().trim()
        val terminalLocation = etTerminalLocation.text.toString().trim()
        
        AuthManager.getInstance(this).setPendingCredentials(email, password, "operator")
        val intent = Intent(this, EmailConfirmationActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        intent.putExtra("userType", "operator")
        intent.putExtra("fullName", fullName)
        intent.putExtra("phone", phone)
        intent.putExtra("terminalName", terminalName)
        intent.putExtra("terminalLocation", terminalLocation)
        startActivity(intent)
        finish()
    }
}
