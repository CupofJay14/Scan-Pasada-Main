package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        findViewById<View>(R.id.tvBackButton1).setOnClickListener {
            val intent = Intent(this@MainActivity2, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tvPassengerLiButton).setOnClickListener {
            android.util.Log.d("MainActivity2", "Passenger login button clicked")
            val intent = Intent(this@MainActivity2, SimpleLoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tvDriverLiButton).setOnClickListener {
            android.util.Log.d("MainActivity2", "Driver login button clicked")
            val intent = Intent(this@MainActivity2, SimpleLoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tvOperatorLiButton).setOnClickListener {
            android.util.Log.d("MainActivity2", "Operator login button clicked")
            val intent = Intent(this@MainActivity2, SimpleLoginActivity::class.java)
            startActivity(intent)
        }
    }
}
