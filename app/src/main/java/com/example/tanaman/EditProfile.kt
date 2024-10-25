package com.example.tanaman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var nameField: EditText
    private lateinit var saveButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        // Bind UI elements
        nameField = view.findViewById(R.id.name_field)
        saveButton = view.findViewById(R.id.save)

        // Load existing name from Firestore if available
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("name")) {
                        nameField.setText(document.getString("name"))
                    }
                }
        }

        // Save button click listener to update name in Firestore
        saveButton.setOnClickListener {
            val newName = nameField.text.toString().trim()

            if (newName.isNotEmpty()) {
                user?.let {
                    val userRef = db.collection("users").document(it.uid)
                    userRef.update("name", newName)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Name saved successfully", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack() // Go back to Profile
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save name", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditProfile()
    }
}
