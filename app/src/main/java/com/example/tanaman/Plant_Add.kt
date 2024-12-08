package com.example.tanaman

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class Plant_Add : Fragment() {

    private lateinit var plantNameEditText: EditText
    private lateinit var plantDescriptionEditText: EditText
    private lateinit var plantCategorySpinner: Spinner
    private lateinit var wateringFrequencyEditText: EditText
    private lateinit var temperatureEditText: EditText
    private lateinit var dangerSwitch: Switch
    private lateinit var lightSeekBar: SeekBar
    private lateinit var savePlantButton: Button
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private lateinit var plantImageView: ImageView
    private var selectedImage: Bitmap? = null
    private var selectedCategory: String = ""

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Launcher for capturing image
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedImage = it
            plantImageView.setImageBitmap(it)
        } ?: Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
    }

    // Launcher for selecting image from gallery
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context?.contentResolver?.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage = bitmap
            plantImageView.setImageBitmap(bitmap)
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
        wateringFrequencyEditText = view.findViewById(R.id.plantWateringFrequencyEditText)
        temperatureEditText = view.findViewById(R.id.plantTemperatureEditText)
        dangerSwitch = view.findViewById(R.id.plantDangerSwitch)
        lightSeekBar = view.findViewById(R.id.lightLevelSeekBar)
        savePlantButton = view.findViewById(R.id.save_plant_button)
        cameraButton = view.findViewById(R.id.camera_button)
        galleryButton = view.findViewById(R.id.gallery_button)
        plantImageView = view.findViewById(R.id.plant_image)

        // Setup categories for spinner
        val categories = listOf(
            "Bedroom",
            "Living Room",
            "Kitchen",
            "Dining Room",
            "Laundry Room"
        )
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        plantCategorySpinner.adapter = adapter

        // Set listener for spinner
        plantCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = categories[0]
            }
        }

        // Camera button functionality
        cameraButton.setOnClickListener { cameraLauncher.launch(null) }

        // Gallery button functionality
        galleryButton.setOnClickListener { galleryLauncher.launch("image/*") }

        // Save plant button functionality
        savePlantButton.setOnClickListener {
            val plantName = plantNameEditText.text.toString()
            val plantDescription = plantDescriptionEditText.text.toString()
            val wateringFrequency = wateringFrequencyEditText.text.toString()
            val temperature = temperatureEditText.text.toString()
            val isDangerous = dangerSwitch.isChecked
            val lightLevel = lightSeekBar.progress

            if (plantName.isNotEmpty() && plantDescription.isNotEmpty() && selectedImage != null) {
                savePlantData(
                    plantName,
                    plantDescription,
                    selectedCategory,
                    wateringFrequency,
                    temperature,
                    isDangerous,
                    lightLevel
                )
            } else {
                Toast.makeText(
                    context,
                    "Please fill all fields and add an image.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
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
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Plant saved successfully!", Toast.LENGTH_SHORT).show()

                // Upload image to Firebase Storage
                selectedImage?.let { image ->
                    uploadToFirebase(image, documentReference.id)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add plant", Toast.LENGTH_SHORT).show()
            }
    }

    // Upload image to Firebase Storage
    private fun uploadToFirebase(image: Bitmap, documentId: String) {
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageData = outputStream.toByteArray()

        val storageRef = storage.reference.child("plant_images/$documentId.jpg")
        storageRef.putBytes(imageData)
            .addOnSuccessListener {
                Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }
}
