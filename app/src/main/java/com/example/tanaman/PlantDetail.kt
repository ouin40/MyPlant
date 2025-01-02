package com.example.tanaman

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PlantDetail : Fragment() {

    private lateinit var plantImageView: ImageView
    private lateinit var backButton: Button
    private lateinit var plantNameTextView: TextView
    private lateinit var plantTemperatureTextView: TextView
    private lateinit var plantWateringFrequencyTextView: TextView
    private lateinit var plantDangerTextView: TextView
    private lateinit var plantLightLevelTextView: TextView
    private lateinit var plantDescriptionTextView: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_detail, container, false)

        // Initialize UI elements
        plantImageView = view.findViewById(R.id.plant_detail_image)
        backButton = view.findViewById(R.id.plant_detail_back_button)
        plantNameTextView = view.findViewById(R.id.plant_detail_name)
        plantTemperatureTextView = view.findViewById(R.id.plant_detail_temperature)
        plantWateringFrequencyTextView = view.findViewById(R.id.plant_detail_watering_frequency)
        plantDangerTextView = view.findViewById(R.id.plant_detail_danger)
        plantLightLevelTextView = view.findViewById(R.id.plant_detail_light_level)
        plantDescriptionTextView = view.findViewById(R.id.plant_detail_description)

        // Back button functionality
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Kembali ke Plant Storage
        }

        // Load data
        val plantId = arguments?.getString("plantId") ?: return view
        loadPlantData(plantId)

        return view
    }

    private fun loadPlantData(plantId: String) {
        firestore.collection("plants").document(plantId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    plantNameTextView.text = document.getString("name")
                    plantTemperatureTextView.text = document.getString("temperature")
                    plantWateringFrequencyTextView.text = document.getString("watering_frequency")
                    plantDangerTextView.text = if (document.getBoolean("danger") == true) "Yes" else "No"
                    plantLightLevelTextView.text = "${document.getLong("light_level") ?: 0}%"
                    plantDescriptionTextView.text = document.getString("description")

                    val imageUrl = document.getString("imageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        loadPlantImage(imageUrl)
                    }
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun loadPlantImage(imageUrl: String) {
        val storageRef = storage.getReferenceFromUrl(imageUrl)
        storageRef.getBytes(1024 * 1024)
            .addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                plantImageView.setImageBitmap(bitmap)
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}
