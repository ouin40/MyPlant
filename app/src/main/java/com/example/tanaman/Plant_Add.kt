package com.example.tanaman

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class Plant_Add : Fragment() {

    private lateinit var backButton: Button
    private lateinit var savePlantButton: Button
    private lateinit var plantNameEditText: EditText
    private lateinit var plantDescriptionEditText: EditText
    private lateinit var plantCategorySpinner: Spinner
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private lateinit var plantImageView: ImageView
    private lateinit var plantDangerSwitch: Switch
    private lateinit var lightLevelSeekBar: SeekBar
    private lateinit var plantTemperatureEditText: EditText
    private lateinit var plantWateringFrequencyEditText: EditText

    private var selectedImage: Bitmap? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val categories = mutableListOf<String>()
    private var selectedCategory: String = ""

    private var isEditMode: Boolean = false
    private var plantId: String? = null


    // Camera Launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedImage = it
            plantImageView.setImageBitmap(it)
        } ?: run {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Launcher
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

        // Periksa apakah halaman ini dibuka dalam mode edit
        val arguments = arguments
        if (arguments != null) {
            isEditMode = arguments.getBoolean("isEditMode", false)
            plantId = arguments.getString("plantId")
            if (isEditMode && plantId != null) {
                loadPlantData(plantId!!)
            }
        }

        // Initialize UI elements
        backButton = view.findViewById(R.id.back_button)
        savePlantButton = view.findViewById(R.id.save_plant_button)
        plantNameEditText = view.findViewById(R.id.plant_name)
        plantDescriptionEditText = view.findViewById(R.id.plantDescriptionEditText)
        plantCategorySpinner = view.findViewById(R.id.plantCategorySpinner)
        cameraButton = view.findViewById(R.id.camera_button)
        galleryButton = view.findViewById(R.id.gallery_button)
        plantImageView = view.findViewById(R.id.plant_image)
        plantDangerSwitch = view.findViewById(R.id.plantDangerSwitch)
        lightLevelSeekBar = view.findViewById(R.id.lightLevelSeekBar)
        plantTemperatureEditText = view.findViewById(R.id.plantTemperatureEditText)
        plantWateringFrequencyEditText = view.findViewById(R.id.plantWateringFrequencyEditText)

        loadCategories()

        cameraButton.setOnClickListener {
            cameraLauncher.launch(null)
        }

        galleryButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        savePlantButton.setOnClickListener {
            val plantName = plantNameEditText.text.toString()
            val plantDescription = plantDescriptionEditText.text.toString()
            val category = selectedCategory.trim()

            if (plantName.isNotEmpty() && plantDescription.isNotEmpty() && selectedImage != null && category.isNotEmpty()) {
                savePlantData(plantName, plantDescription, category)
            } else {
                Toast.makeText(context, "Please fill all fields and select a category.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadPlantData(plantId: String) {
        firestore.collection("plants").document(plantId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    plantNameEditText.setText(document.getString("name"))
                    plantDescriptionEditText.setText(document.getString("description"))
                    plantCategorySpinner.setSelection(categories.indexOf(document.getString("category")))
                    plantDangerSwitch.isChecked = document.getBoolean("danger") == true
                    lightLevelSeekBar.progress = (document.getLong("light_level") ?: 0).toInt()
                    plantTemperatureEditText.setText(document.getString("temperature"))
                    plantWateringFrequencyEditText.setText(document.getString("watering_frequency"))

                    val imageUrl = document.getString("imageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        // Unduh gambar untuk ditampilkan
                        downloadImage(imageUrl)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load plant data", Toast.LENGTH_SHORT).show()
            }
    }


    private fun downloadImage(url: String) {
        val storageRef = storage.getReferenceFromUrl(url)
        storageRef.getBytes(1024 * 1024)
            .addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                selectedImage = bitmap
                plantImageView.setImageBitmap(bitmap)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load plant image", Toast.LENGTH_SHORT).show()
            }
    }



    private fun loadCategories() {
        firestore.collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                categories.clear()
                categories.addAll(listOf("Kitchen", "Bedroom", "Laundry Room", "Living Room"))
                for (document in documents) {
                    val categoryName = document.getString("name") ?: ""
                    categories.add(categoryName)
                }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                plantCategorySpinner.adapter = adapter

                plantCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedCategory = categories[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedCategory = ""
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePlantData(name: String, description: String, category: String) {
        val danger = plantDangerSwitch.isChecked
        val lightLevel = lightLevelSeekBar.progress
        val temperature = plantTemperatureEditText.text.toString()
        val wateringFrequency = plantWateringFrequencyEditText.text.toString()

        val updatedData = mutableMapOf<String, Any>(
            "name" to name,
            "description" to description,
            "category" to category,
            "danger" to danger,
            "light_level" to lightLevel,
            "temperature" to temperature,
            "watering_frequency" to wateringFrequency
        )

        if (isEditMode && plantId != null) {
            // Perbarui dokumen yang ada
            firestore.collection("plants").document(plantId!!)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Plant updated successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update plant", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Tambahkan dokumen baru
            selectedImage?.let { image ->
                val storageRef = storage.reference.child("plants/images/${System.currentTimeMillis()}.jpg")
                val baos = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                storageRef.putBytes(data)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            updatedData["imageUrl"] = uri.toString()
                            firestore.collection("plants")
                                .add(updatedData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Plant added successfully", Toast.LENGTH_SHORT).show()
                                    requireActivity().supportFragmentManager.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to add plant", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}