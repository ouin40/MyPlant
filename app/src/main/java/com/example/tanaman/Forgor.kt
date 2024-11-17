package com.example.tanaman

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Forgor : AppCompatActivity() {

    lateinit var buttonForgor: Button
    lateinit var editTextEmail: TextInputEditText
    lateinit var auth: FirebaseAuth
    lateinit var textViewLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgor)

        buttonForgor = findViewById(R.id.btn_forgor)
        editTextEmail = findViewById(R.id.email)
        auth = FirebaseAuth.getInstance()
        textViewLogin = findViewById(R.id.loginNow)

        textViewLogin.setOnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        buttonForgor.setOnClickListener {
            val email = editTextEmail.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmail.error = "Email is required"
                editTextEmail.requestFocus()
                Toast.makeText(this@Forgor, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText( this, "Check your email to Reset Password", Toast.LENGTH_SHORT).show()
                    Intent(this@Forgor, Login::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText( this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}