package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcomepage)

        // Test database connection on app startup
        testDatabaseConnection()

        findViewById<View>(R.id.tvLoginButton).setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tvSignupButton).setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity3::class.java)
            startActivity(intent)
        }
    }

    private fun testDatabaseConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                android.util.Log.d("MainActivity", "=== TESTING DATABASE CONNECTION ON STARTUP ===")
                val simpleAuthService = SimpleAuthService()
                
                // Test database insert operation
                val insertTest = simpleAuthService.testInsertOperation()
                android.util.Log.d("MainActivity", "Database insert test result: $insertTest")
                
                if (insertTest) {
                    android.util.Log.d("MainActivity", "✅ Database connection test passed!")
                } else {
                    android.util.Log.e("MainActivity", "❌ Database connection test failed!")
                    android.util.Log.e("MainActivity", "Please check your database configuration.")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error testing database connection: ${e.message}", e)
                android.util.Log.e("MainActivity", "Stack trace: ${e.stackTrace.joinToString("\n")}")
            }
        }
    }
}