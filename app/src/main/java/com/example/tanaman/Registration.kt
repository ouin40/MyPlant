package com.example.tanaman

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registration : AppCompatActivity() {
    lateinit var editTextName: TextInputEditText
    lateinit var editTextEmail: TextInputEditText
    lateinit var editTextPassword: TextInputEditText
    lateinit var buttonReg: Button
    lateinit var auth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    lateinit var textView: TextView
    private val db = FirebaseFirestore.getInstance()

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Intent(applicationContext, MainActivity::class.java).also {
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextName = findViewById(R.id.name)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonReg = findViewById(R.id.btn_register)
        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.loginNow)

        textView.setOnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        buttonReg.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (name.isEmpty()) {
                editTextName.error = "Name is required"
                editTextName.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                editTextEmail.error = "Email is required"
                editTextEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                editTextPassword.error = "Password is required"
                editTextPassword.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid ?: ""
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email
                        )

                        db.collection("users").document(userId).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this@Registration, "Account created.", Toast.LENGTH_SHORT).show()
                                Intent(applicationContext, MainActivity::class.java).also {
                                    startActivity(it)
                                    finish()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@Registration, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@Registration,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}
