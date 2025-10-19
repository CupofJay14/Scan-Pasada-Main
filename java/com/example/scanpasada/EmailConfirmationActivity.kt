package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class EmailConfirmationActivity : AppCompatActivity() {

    private lateinit var tvEmail: TextView
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar
    private var email: String? = null
    private var password: String? = null
    private var userType: String? = null
    private var fullName: String? = null
    private var phone: String? = null
    private var terminalName: String? = null
    private var terminalLocation: String? = null
    private var licenseNumber: String? = null
    private var plateNumber: String? = null
    private var age: Int = -1
    private var address: String? = null
    
    private var confirmationCheckJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private var checkCount = 0
    private val maxChecks = 60 // Check for 5 minutes (60 * 5 seconds)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_confirmation)

        initializeViews()
        extractIntentData()
        startAutomaticConfirmationProcess()
    }

    private fun initializeViews() {
        tvEmail = findViewById(R.id.tvEmailValue)
        tvStatus = findViewById(R.id.tvStatus)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun extractIntentData() {
        email = intent.getStringExtra("email")
        password = intent.getStringExtra("password")
        userType = intent.getStringExtra("userType")
        
        // Extract user data from the map
        val userData = intent.getSerializableExtra("userData") as? HashMap<String, Any>
        if (userData != null) {
            fullName = userData["fullName"] as? String
            phone = userData["phone"] as? String
            terminalName = userData["terminalName"] as? String
            terminalLocation = userData["terminalLocation"] as? String
            licenseNumber = userData["licenseNumber"] as? String
            plateNumber = userData["plateNumber"] as? String
            age = userData["age"] as? Int ?: -1
            address = userData["address"] as? String
        }

        tvEmail.text = email ?: "Unknown"
    }

    private fun startAutomaticConfirmationProcess() {
        // Automatically send confirmation email
        sendConfirmationEmail()
        
        // Start checking for email confirmation
        startConfirmationChecking()
        
        // Add manual confirmation check button
        findViewById<TextView>(R.id.tvStatus).setOnClickListener {
            android.util.Log.d("EmailConfirmation", "Manual confirmation check triggered")
            checkConfirmationManually()
        }
        
        // Add long press for skip confirmation (debugging only)
        findViewById<TextView>(R.id.tvStatus).setOnLongClickListener {
            android.util.Log.d("EmailConfirmation", "Skip confirmation triggered (debug mode)")
            showSkipConfirmationDialog()
            true
        }
    }

    private fun sendConfirmationEmail() {
        tvStatus.text = "Sending confirmation email..."
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = SupabaseService()
                val success = service.sendConfirmationEmail(email ?: "")
                
                withContext(Dispatchers.Main) {
                    if (success) {
                        tvStatus.text = "Confirmation email sent! Please check your inbox and click the confirmation link."
                    } else {
                        tvStatus.text = "Failed to send confirmation email. Please try again."
                        Toast.makeText(this@EmailConfirmationActivity, 
                            "Unable to send confirmation email. Please check your internet connection.", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvStatus.text = "Error sending confirmation email."
                    Toast.makeText(this@EmailConfirmationActivity, 
                        "Error: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startConfirmationChecking() {
        tvStatus.text = "Waiting for email confirmation...\nTap here to check manually after confirming your email"
        progressBar.visibility = ProgressBar.VISIBLE
        
        confirmationCheckJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive && checkCount < maxChecks) {
                delay(5000) // Check every 5 seconds
                checkCount++
                
                try {
                    val isConfirmed = checkEmailConfirmation()
                    
                withContext(Dispatchers.Main) {
                    if (isConfirmed) {
                        handleEmailConfirmed()
                        return@withContext
                    } else {
                        tvStatus.text = "Waiting for email confirmation... (${checkCount}/$maxChecks)\nTap here to check manually after confirming your email"
                    }
                }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        tvStatus.text = "Error checking confirmation status: ${e.message}"
                    }
                }
            }
            
            // Timeout reached
            withContext(Dispatchers.Main) {
                tvStatus.text = "Confirmation timeout. Please try again or contact support."
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@EmailConfirmationActivity, 
                    "Email confirmation timeout. Please try again.", 
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun checkEmailConfirmation(): Boolean {
        return try {
            val service = SupabaseService()
            android.util.Log.d("EmailConfirmation", "Checking email confirmation for: $email")
            
            // First check if the user exists in the auth system
            val userExists = service.checkUserExists(email ?: "")
            android.util.Log.d("EmailConfirmation", "User exists check: $userExists")
            
            if (!userExists) {
                android.util.Log.d("EmailConfirmation", "User does not exist in auth system")
                return false
            }
            
            // Use the improved confirmation status check from SupabaseService
            // This now uses multiple approaches to verify confirmation status
            val confirmationStatus = service.checkEmailConfirmationStatus(email ?: "")
            android.util.Log.d("EmailConfirmation", "Email confirmation status: $confirmationStatus")
            
            // Force a refresh of the check after a short delay if not confirmed
            if (!confirmationStatus) {
                delay(1000) // Wait 1 second
                val secondCheck = service.checkEmailConfirmationStatus(email ?: "")
                android.util.Log.d("EmailConfirmation", "Second email confirmation check: $secondCheck")
                return secondCheck
            }
            
            return confirmationStatus
        } catch (e: Exception) {
            android.util.Log.e("EmailConfirmation", "Error in checkEmailConfirmation: ${e.message}")
            false
        }
    }

    private fun checkConfirmationManually() {
        tvStatus.text = "Checking email confirmation manually..."
        progressBar.visibility = ProgressBar.VISIBLE
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isConfirmed = checkEmailConfirmation()
                
                withContext(Dispatchers.Main) {
                    if (isConfirmed) {
                        android.util.Log.d("EmailConfirmation", "Manual check successful - email confirmed!")
                        handleEmailConfirmed()
                    } else {
                        tvStatus.text = "Email not confirmed yet. Please check your email and click the confirmation link, then tap here again."
                        progressBar.visibility = ProgressBar.GONE
                        Toast.makeText(this@EmailConfirmationActivity, 
                            "Email not confirmed yet. Please check your email and try again.", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvStatus.text = "Error checking confirmation: ${e.message}"
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@EmailConfirmationActivity, 
                        "Error checking confirmation: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSkipConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Skip Email Confirmation")
            .setMessage("Are you sure you want to skip email confirmation? This should only be used if you've already confirmed your email but the app didn't detect it.")
            .setPositiveButton("Yes, Skip") { _, _ ->
                android.util.Log.d("EmailConfirmation", "User chose to skip confirmation")
                handleEmailConfirmed()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleEmailConfirmed() {
        confirmationCheckJob?.cancel()
        progressBar.visibility = ProgressBar.VISIBLE
        tvStatus.text = "Verifying email confirmation..."
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Implement a retry mechanism for email confirmation check
                var isConfirmed = false
                val service = SupabaseService()
                
                // Try up to 3 times with delays between attempts
                for (attempt in 1..3) {
                    android.util.Log.d("EmailConfirmation", "Confirmation check attempt $attempt")
                    isConfirmed = service.checkEmailConfirmationStatus(email ?: "")
                    
                    if (isConfirmed) {
                        android.util.Log.d("EmailConfirmation", "Email confirmed on attempt $attempt")
                        break
                    } else if (attempt < 3) {
                        // Update UI to show we're retrying
                        withContext(Dispatchers.Main) {
                            tvStatus.text = "Verifying email confirmation... Attempt $attempt/3"
                        }
                        delay(1500) // Wait 1.5 seconds between attempts
                    }
                }
                
                if (!isConfirmed) {
                    withContext(Dispatchers.Main) {
                        tvStatus.text = "Email is not yet confirmed in Supabase."
                        progressBar.visibility = ProgressBar.GONE
                        Toast.makeText(this@EmailConfirmationActivity, 
                            "Your email is not yet confirmed. Please check your inbox and click the confirmation link.", 
                            Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }
                
                // Email is confirmed, proceed with account setup
                withContext(Dispatchers.Main) {
                    tvStatus.text = "Email confirmed! Setting up your account..."
                }
                
                // Use SimpleAuthManager to complete signup after email confirmation
                val authManager = SimpleAuthManager(this@EmailConfirmationActivity)
                
                // Prepare user data map
                val userData = mapOf(
                    "fullName" to (fullName ?: ""),
                    "phone" to (phone ?: ""),
                    "licenseNumber" to (licenseNumber ?: ""),
                    "plateNumber" to (plateNumber ?: ""),
                    "age" to age,
                    "address" to (address ?: ""),
                    "terminalName" to (terminalName ?: ""),
                    "terminalLocation" to (terminalLocation ?: "")
                )
                
                android.util.Log.d("EmailConfirmation", "Completing signup with SimpleAuthManager")
                android.util.Log.d("EmailConfirmation", "Email: $email, UserType: $userType")
                android.util.Log.d("EmailConfirmation", "UserData: $userData")
                
                val success = authManager.completeSignUp(
                    email ?: "", 
                    password ?: "", 
                    userType ?: "", 
                    userData
                )
                
                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(this@EmailConfirmationActivity, 
                            "Account setup complete! Welcome!", 
                            Toast.LENGTH_SHORT).show()
                        
                        // Navigate to appropriate home page
                        val intent = when (userType) {
                            "driver" -> Intent(this@EmailConfirmationActivity, DriverHomePage::class.java)
                            "passenger" -> Intent(this@EmailConfirmationActivity, PassengerHomePage::class.java)
                            "operator" -> Intent(this@EmailConfirmationActivity, OperatorHomePage::class.java)
                            else -> Intent(this@EmailConfirmationActivity, MainActivity2::class.java)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = authManager.lastErrorMessage ?: "Account setup failed"
                        Toast.makeText(this@EmailConfirmationActivity, 
                            "Error: $errorMessage", 
                            Toast.LENGTH_LONG).show()
                        tvStatus.text = "Error: $errorMessage"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EmailConfirmationActivity, 
                        "Error during account setup: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                    tvStatus.text = "Error: ${e.message}"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        confirmationCheckJob?.cancel()
    }
}