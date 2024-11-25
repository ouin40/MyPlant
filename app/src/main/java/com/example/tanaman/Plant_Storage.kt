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
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import android.content.pm.PackageManager


class Plant_Storage : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addPlantButton: Button
    private val categories = arrayListOf<Category>()
    private val storage = FirebaseStorage.getInstance()
    private val dummyCategoryIndex = 0 // Indeks kategori untuk dummy data

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    // Launcher untuk mengambil gambar
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

        // Dummy data untuk kategori dan tanaman
        val samplePlants1 = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.plant1),
            BitmapFactory.decodeResource(resources, R.drawable.plant2),
            BitmapFactory.decodeResource(resources, R.drawable.plant3)
        )
        categories.add(Category("Dapur", samplePlants1.toMutableList()))

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CategoryAdapter(categories)

        // Mengatur click listener untuk tombol add plant
        addPlantButton.setOnClickListener {
            // Meluncurkan kamera untuk mengambil gambar
            cameraLauncher.launch(null)
        }

        return view
    }

    // Fungsi untuk mengupload gambar ke Firebase
    private fun uploadToFirebase(bitmap: Bitmap) {
        val storageRef = storage.reference.child("plants/${System.currentTimeMillis()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Mengupload gambar ke Firebase Storage
        storageRef.putBytes(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Photo uploaded!", Toast.LENGTH_SHORT).show()
                categories[dummyCategoryIndex].plants.add(bitmap)
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upload failed!", Toast.LENGTH_SHORT).show()
            }
    }

    // Menangani hasil izin yang diminta
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
