package com.example.tanaman

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tanaman.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Firebase Authentication
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        if (user == null) {
            // Jika user belum login, redirect ke layar Login
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            // Menampilkan email user (opsional)
            // binding.userDetails.text = user?.email
            // Ganti fragment home jika user sudah login
            replaceFragment(Home())
        }

        // Setup listener untuk BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.kalender -> replaceFragment(Kalender())
                R.id.plant -> replaceFragment(Plant_Storage())
                R.id.profile -> replaceFragment(Profile())
                else -> false
            }
            true
        }
    }

    // Fungsi helper untuk mengganti fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
