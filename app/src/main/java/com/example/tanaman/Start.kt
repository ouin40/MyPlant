package com.example.tanaman

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Start : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Button to start the login activity
        val startButton: Button = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            // Start Login activity manually when the button is clicked
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()  // Close the splash screen activity
        }
    }
}
