package com.example.tanaman

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Profile : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var userNameTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var logoutButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        // Bind UI elements
        userNameTextView = view.findViewById(R.id.user_name)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        logoutButton = view.findViewById(R.id.logout)

        // Load user name from Firestore
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("name")) {
                        userNameTextView.text = document.getString("name") ?: "No Name"
                    } else {
                        userNameTextView.text = "No Name"
                    }
                }
        }

        // Set edit profile button to open EditProfile fragment
        editProfileButton.setOnClickListener {
            // Navigate to EditProfile fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, EditProfile.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // Set logout button to sign out
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = Profile()
    }
}
