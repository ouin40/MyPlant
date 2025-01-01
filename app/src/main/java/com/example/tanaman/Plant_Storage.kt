package com.example.tanaman

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Plant_Storage : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addPlantButton: Button
    private val categories = arrayListOf<Category>()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d("Plant_Storage", "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_plant_storage, container, false)

        recyclerView = view.findViewById(R.id.category_recycler_view)
        addPlantButton = view.findViewById(R.id.addPlant)

        loadCategoriesAndPlants()

        addPlantButton.setOnClickListener {
            navigateToAddPlant()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (::addPlantButton.isInitialized) {
            addPlantButton.visibility = View.VISIBLE
            Log.d("Plant_Storage", "Add Plant button set to VISIBLE in onResume")
        }
    }

    private fun navigateToAddPlant() {
        addPlantButton.visibility = View.GONE // Sembunyikan tombol sementara
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, Plant_Add())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Fungsi untuk memuat kategori dan data tanaman dari Firestore
    private fun loadCategoriesAndPlants() {
        lifecycleScope.launch {
            try {
                val predefinedCategories = listOf("Kitchen", "Bedroom", "Laundry Room", "Living Room")
                    .map { it.trim().lowercase() } // Standarisasi

                val categoryMap = predefinedCategories.associateWith { mutableListOf<Pair<String, Bitmap>>() }.toMutableMap()

                val documents = firestore.collection("plants").get().await()

                for (document in documents) {
                    val category = document.getString("category")?.trim()?.lowercase() ?: "uncategorized"
                    val imageUrl = document.getString("imageUrl")
                    val plantName = document.getString("name") ?: "Unnamed Plant"

                    Log.d("Firestore", "Plant Name: $plantName, Image URL: $imageUrl")

                    if (imageUrl.isNullOrEmpty()) {
                        Log.e("Plant_Storage", "Image URL is missing for document: ${document.id}")
                        continue // Lewati dokumen ini jika URL kosong
                    }

                    Log.d("Plant_Storage", "Document category: $category") // Debugging

                    if (predefinedCategories.contains(category)) {
                        val bitmap = downloadImage(imageUrl)
                        bitmap?.let { categoryMap[category]?.add(Pair(plantName, it)) }
                    } else {
                        val bitmap = downloadImage(imageUrl)
                        bitmap?.let { categoryMap.getOrPut("uncategorized") { mutableListOf() }.add(Pair(plantName, it)) }
                    }
                }

                categories.clear()
                for ((categoryName, plantList) in categoryMap) {
                    categories.add(Category(categoryName.replaceFirstChar { it.uppercase() }, plantList))
                }

                withContext(Dispatchers.Main) {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = CategoryAdapter(categories)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to load plants: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private suspend fun downloadImage(url: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                if (url.isNullOrEmpty()) {
                    Log.e("Plant_Storage", "Image URL is null or empty")
                    return@withContext null
                }
                val ref = storage.getReferenceFromUrl(url)
                val bytes = ref.getBytes(1024 * 1024).await()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    // permintaan izin kamera
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}
