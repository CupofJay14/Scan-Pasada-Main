package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PassengerLoginPage : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvLogin: TextView
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_login_page)

        authManager = AuthManager.getInstance(this)
        
        // Check if already logged in
        if (authManager.isLoggedIn() && "passenger" == authManager.getUserType()) {
            startActivity(Intent(this, PassengerHomePage::class.java))
            finish()
            return
        }

        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        etEmail = findViewById(R.id.etPassengerEmailLI)
        etPassword = findViewById(R.id.etPassengerPasswordLi)
        tvLogin = findViewById(R.id.tvPassengerLoginButton)
    }
    
    private fun setupClickListeners() {
        // Back button
        findViewById<TextView>(R.id.tvBackButton6).setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }

        // Login button
        tvLogin.setOnClickListener { handleLogin() }
        
        // Sign up link
        findViewById<TextView>(R.id.tvSignUpLink).setOnClickListener {
            val intent = Intent(this, PassengerSignUpForm::class.java)
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

        if (authManager.login(email, password, "passenger")) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, PassengerHomePage::class.java))
            finish()
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }
}
