package com.example.tanaman

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.graphics.BitmapFactory
import androidx.activity.result.contract.ActivityResultContracts
import java.io.ByteArrayOutputStream

class Plant_Add : Fragment() {

    private lateinit var plantNameEditText: EditText
    private lateinit var plantDescriptionEditText: EditText
    private lateinit var plantCategorySpinner: Spinner
    private lateinit var plantWateringFrequencyEditText: EditText
    private lateinit var plantTemperatureEditText: EditText
    private lateinit var plantDangerSwitch: Switch
    private lateinit var lightLevelSeekBar: SeekBar
    private lateinit var savePlantButton: Button
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private lateinit var plantImageView: ImageView
    private var selectedImage: Bitmap? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val categories = mutableListOf<String>()
    private var selectedCategory: String = ""

    // Camera launcher to capture image
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedImage = it
            plantImageView.setImageBitmap(it) // Set captured image to ImageView
        } ?: run {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery launcher to pick image
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context?.contentResolver?.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage = bitmap
            plantImageView.setImageBitmap(bitmap) // Set selected image to ImageView
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_add, container, false)

        // Initialize views
        plantNameEditText = view.findViewById(R.id.plant_name)
        plantDescriptionEditText = view.findViewById(R.id.plantDescriptionEditText)
        plantCategorySpinner = view.findViewById(R.id.plantCategorySpinner)
        plantWateringFrequencyEditText = view.findViewById(R.id.plantWateringFrequencyEditText)
        plantTemperatureEditText = view.findViewById(R.id.plantTemperatureEditText)
        plantDangerSwitch = view.findViewById(R.id.plantDangerSwitch)
        lightLevelSeekBar = view.findViewById(R.id.lightLevelSeekBar)
        savePlantButton = view.findViewById(R.id.save_plant_button)
        cameraButton = view.findViewById(R.id.camera_button)
        galleryButton = view.findViewById(R.id.gallery_button)
        plantImageView = view.findViewById(R.id.plant_image)

        // Load categories from Firestore
        loadCategories()

        // Spinner item selection listener
        plantCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedCategory = categories[0] // Default to first category
            }
        }

        // Open camera when button is clicked
        cameraButton.setOnClickListener {
            cameraLauncher.launch(null)
        }

        // Open gallery when button is clicked
        galleryButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Save plant data when button is clicked
        savePlantButton.setOnClickListener {
            val plantName = plantNameEditText.text.toString()
            val plantDescription = plantDescriptionEditText.text.toString()
            val wateringFrequency = plantWateringFrequencyEditText.text.toString()
            val temperature = plantTemperatureEditText.text.toString()
            val isDangerous = plantDangerSwitch.isChecked
            val lightLevel = lightLevelSeekBar.progress

            if (plantName.isNotEmpty() && plantDescription.isNotEmpty() && selectedImage != null) {
                savePlantData(plantName, plantDescription, selectedCategory, wateringFrequency, temperature, isDangerous, lightLevel)
            } else {
                Toast.makeText(context, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Load plant categories from Firestore
    private fun loadCategories() {
        firestore.collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                categories.clear()
                for (document in documents) {
                    val categoryName = document.getString("name") ?: ""
                    categories.add(categoryName)
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                plantCategorySpinner.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
    }

    // Save plant data to Firestore
    private fun savePlantData(
        name: String, description: String, category: String,
        watering: String, temperature: String, danger: Boolean, light: Int
    ) {
        val plantData = mapOf(
            "name" to name,
            "description" to description,
            "category" to category,
            "watering_frequency" to watering,
            "temperature" to temperature,
            "danger" to danger,
            "light_level" to light
        )

        firestore.collection("plants")
            .add(plantData)
            .addOnSuccessListener {
                selectedImage?.let { image -> uploadToFirebase(image) } // Upload image after saving data
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add plant", Toast.LENGTH_SHORT).show()
            }
    }

    // Upload image to Firebase Storage
    private fun uploadToFirebase(bitmap: Bitmap) {
        val storageRef = storage.reference.child("plants/images/${System.currentTimeMillis()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }
}
