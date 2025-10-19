package com.example.scanpasada

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signuppage)

        findViewById<View>(R.id.tvBackButton2).setOnClickListener {
            val intent = Intent(this@MainActivity3, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tvDriverSuButton).setOnClickListener {
            android.util.Log.d("MainActivity3", "Driver signup button clicked")
            val intent = Intent(this@MainActivity3, DriverSignUpFormPage::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tvOperatorSuButton).setOnClickListener { view ->
            android.util.Log.d("MainActivity3", "Operator signup button clicked")
            val intent = Intent(this@MainActivity3, OperatorSignUpFormActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.tvPassengerSuButton).setOnClickListener { view ->
            android.util.Log.d("MainActivity3", "Passenger signup button clicked")
            val intent = Intent(this@MainActivity3, PassengerSignUpForm::class.java)
            startActivity(intent)
        }
    }
}
