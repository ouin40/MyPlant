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
import android.util.Log

class Plant_Storage : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val categories = arrayListOf<Category>()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_storage, container, false)

        recyclerView = view.findViewById(R.id.category_recycler_view)

        // Set up RecyclerView with a LinearLayoutManager and adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Make sure to set the adapter after the data is ready
        fetchCategoriesFromFirestore()

        return view
    }

    private fun fetchCategoriesFromFirestore() {
        firestore.collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                categories.clear()
                for (document in documents) {
                    val categoryName = document.getString("name") ?: ""

                    // Example: Fetching plants data from Firestore under each category
                    val plantList = mutableListOf<Plant>()
                    // Assuming plants are stored in sub-collection "plants" for each category
                    val plantsCollection = document.reference.collection("plants")
                    plantsCollection.get().addOnSuccessListener { plantDocuments ->
                        for (plantDocument in plantDocuments) {
                            val plantName = plantDocument.getString("name") ?: ""
                            val plantDescription = plantDocument.getString("description") ?: ""
                            val plantLightLevel = plantDocument.getLong("light_level")?.toInt() ?: 0
                            val plantTemperature = plantDocument.getString("temperature") ?: ""
                            val plantWateringFrequency =
                                plantDocument.getString("watering_frequency") ?: ""

                            val plant = Plant(
                                plantName,
                                plantDescription,
                                plantLightLevel,
                                plantTemperature,
                                plantWateringFrequency,
                                "" // You can pass an empty string or a valid URL here
                            )

                            plantList.add(plant)
                        }

                        // Now, set up the category with a list of Plant objects
                        categories.add(Category(categoryName, plantList))

                        // Set the adapter after data is fetched
                        recyclerView.adapter =
                            CategoryAdapter(categories, requireContext()) // Pass context here

                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
