package com.example.tanaman

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Profile : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userProfileImageView: ImageView
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
        userEmailTextView = view.findViewById(R.id.user_email)
        userProfileImageView = view.findViewById(R.id.profile_image)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        logoutButton = view.findViewById(R.id.logout)

        // Load user name, email, and profile image from Firestore
        loadUserProfile()

        // Set edit profile button to open EditProfile fragment
        editProfileButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, EditProfile.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // Set logout button to sign out
        logoutButton.setOnClickListener {
            auth.signOut()

            val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("isFirstLaunch")
            editor.apply()

            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }

    private fun loadUserProfile() {
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Load name
                        val name = document.getString("name") ?: "No Name"
                        userNameTextView.text = name

                        // Load email
                        val email = document.getString("email") ?: it.email ?: "No Email"
                        userEmailTextView.text = email

                        // Load profile image
                        document.getString("profileImage")?.let { imageUrl ->
                            val imageUri = Uri.parse(imageUrl)
                            loadProfileImage(imageUri)  // Call the method to load image as Bitmap
                        } ?: run {
                            userProfileImageView.setImageResource(R.drawable.baseline_account_circle_24)
                        }
                    } else {
                        userNameTextView.text = "No Name"
                        userEmailTextView.text = "No Email"
                        userProfileImageView.setImageResource(R.drawable.baseline_account_circle_24)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to load profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadProfileImage(imageUri: Uri) {
        // Get the reference to Firebase Storage
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri.toString())

        // Download the image as a Bitmap
        storageReference.getBytes(Long.MAX_VALUE) // Download the image bytes
            .addOnSuccessListener { bytes ->
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                userProfileImageView.setImageBitmap(bitmap)  // Set the downloaded Bitmap to the ImageView
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        // Reload user profile data when the fragment is resumed
        loadUserProfile()
    }

    companion object {
        @JvmStatic
        fun newInstance() = Profile()
    }
}
