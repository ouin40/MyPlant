package com.example.tanaman

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import android.content.pm.PackageManager

class Plant_Storage : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addPlantButton: Button
    private val categories = arrayListOf<Category>()
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private var dummyCategoryIndex: Int = -1  // Ensure this is properly set before use

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            uploadToFirebase(bitmap)
        } else {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_storage, container, false)

        recyclerView = view.findViewById(R.id.category_recycler_view)
        addPlantButton = view.findViewById(R.id.addPlant)

        // Meminta izin untuk menggunakan kamera jika belum diberikan
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        // Ambil kategori tanaman dari Firestore
        firestore.collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                categories.clear()
                for (document in documents) {
                    val categoryName = document.getString("name") ?: ""
                    val plantList = mutableListOf<Bitmap>()
                    categories.add(Category(categoryName, plantList))
                }
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = CategoryAdapter(categories)
                dummyCategoryIndex = 0
            }

        // Mengatur click listener untuk tombol add plant
        addPlantButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, Plant_Add())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun uploadToFirebase(bitmap: Bitmap) {
        val storageRef = storage.reference.child("plants/${System.currentTimeMillis()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Photo uploaded!", Toast.LENGTH_SHORT).show()

                // Ensure dummyCategoryIndex is valid before using it
                if (dummyCategoryIndex >= 0 && dummyCategoryIndex < categories.size) {
                    categories[dummyCategoryIndex].plants.add(bitmap)
                    recyclerView.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Category not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upload failed!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Provide an explanation or guide user to settings
                Toast.makeText(context, "Camera permission denied. Please allow permission to capture images.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
