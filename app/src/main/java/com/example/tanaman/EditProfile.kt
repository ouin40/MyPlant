package com.example.tanaman

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditProfile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var nameField: EditText
    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var storageReference: StorageReference

    // Define a constant for image picking
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        storageReference = FirebaseStorage.getInstance().reference

        // Bind UI elements
        nameField = view.findViewById(R.id.name_field)
        saveButton = view.findViewById(R.id.save)
        imageView = view.findViewById(R.id.profile_image)

        // Open gallery to select image when imageView is clicked
        imageView.setOnClickListener {
            openGallery()
        }

        // Load existing name from Firestore if available
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("name")) {
                        nameField.setText(document.getString("name"))
                        document.getString("profileImage")?.let {
                            // If image URL exists, load image manually
                            val imageUri = Uri.parse(it)
                            imageView.setImageURI(imageUri) // Directly set image URI
                        }
                    }
                }
        }

        // Save button click listener to update name in Firestore
        saveButton.setOnClickListener {
            val newName = nameField.text.toString().trim()

            if (newName.isNotEmpty()) {
                user?.let {
                    val userRef = db.collection("users").document(it.uid)
                    userRef.set(mapOf("name" to newName), SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(context, "Name saved successfully", Toast.LENGTH_SHORT)
                                .show()
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

    // Function to open the gallery to pick an image
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle the selected image and upload to Firebase Storage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                // Show selected image in ImageView
                imageView.setImageURI(it)

                // Upload the image to Firebase Storage
                uploadImageToFirebase(it)
            }
        }
    }

    // Function to upload selected image to Firebase Storage
    private fun uploadImageToFirebase(uri: Uri) {
        val user = auth.currentUser
        user?.let {
            val filePath = storageReference.child("profile_pictures/${user.uid}.jpg")
            filePath.putFile(uri)
                .addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Save the download URL in Firestore
                        saveProfileImageUrlToFirestore(downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to save the image URL to Firestore
    private fun saveProfileImageUrlToFirestore(imageUrl: String) {
        val user = auth.currentUser
        user?.let {
            val userRef = db.collection("users").document(it.uid)
            userRef.update("profileImage", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save image URL", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditProfile()
    }
}
