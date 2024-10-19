package com.example.tanaman

import android.content.Intent
import android.os.Bundle
import android.view.autofill.AutofillManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var button: Button
    lateinit var textView: TextView
    lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        button = findViewById(R.id.logout)
        textView = findViewById(R.id.user_details)
        val user = auth.currentUser

        if(user == null) {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
        else {
            textView.setText(user.email)
        }

        button.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}