package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class DriverLoginPage : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvLogin: TextView
    private lateinit var authManager: SimpleAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login_page)

        authManager = SimpleAuthManager(this)
        
        // Check if already logged in
        if (authManager.loadCurrentUser() && authManager.currentUser?.userType == "driver") {
            startActivity(Intent(this, DriverHomePage::class.java))
            finish()
            return
        }

        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        etEmail = findViewById(R.id.etDriverEmailLI)
        etPassword = findViewById(R.id.etDriverPasswordLi)
        tvLogin = findViewById(R.id.tvDriverLoginButton)
    }
    
    private fun setupClickListeners() {
        // Back button
        findViewById<TextView>(R.id.tvBackButton7).setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }

        // Login button
        tvLogin.setOnClickListener { handleLogin() }
        
        // Sign up link
        findViewById<TextView>(R.id.tvSignUpLink).setOnClickListener {
            val intent = Intent(this, DriverSignUpFormPage::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        tvLogin.isEnabled = false
        tvLogin.text = "Signing In..."

        // Perform login in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = authManager.login(email, password, "driver")

                withContext(Dispatchers.Main) {
                    tvLogin.isEnabled = true
                    tvLogin.text = "Login"

                    if (success) {
                        Toast.makeText(this@DriverLoginPage, "Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@DriverLoginPage, DriverHomePage::class.java))
                        finish()
                    } else {
                        val errorMessage = authManager.lastErrorMessage ?: "Login failed. Please try again."
                        Toast.makeText(this@DriverLoginPage, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvLogin.isEnabled = true
                    tvLogin.text = "Login"
                    Toast.makeText(this@DriverLoginPage, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
