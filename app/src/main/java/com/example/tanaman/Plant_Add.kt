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
    private var selectedImage: Bitmap? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val categories = mutableListOf<String>()
    private var selectedCategory: String = ""

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

    // Galerry Launcher
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

        // Initialize UI elements
        backButton = view.findViewById(R.id.back_button)
        savePlantButton = view.findViewById(R.id.save_plant_button)
        plantNameEditText = view.findViewById(R.id.plant_name)
        plantDescriptionEditText = view.findViewById(R.id.plantDescriptionEditText)
        plantCategorySpinner = view.findViewById(R.id.plantCategorySpinner)
        cameraButton = view.findViewById(R.id.camera_button)
        galleryButton = view.findViewById(R.id.gallery_button)
        plantImageView = view.findViewById(R.id.plant_image)

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

    // Fungsi untuk memuat kategori dari Firestore
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

                // Listener untuk spinner
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

    // Fungsi untuk menyimpan data tanaman ke Firestore
    private fun savePlantData(name: String, description: String, category: String) {
        Log.d("Plant_Add", "Saving plant with category: $category") // Debug kategori

        val plantData = mapOf(
            "name" to name,
            "description" to description,
            "category" to category,
            "imageUrl" to ""
        )

        selectedImage?.let { image ->
            val storageRef = storage.reference.child("plants/images/${System.currentTimeMillis()}.jpg")
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            storageRef.putBytes(data)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val updatedPlantData = plantData.toMutableMap()
                        updatedPlantData["imageUrl"] = uri.toString()
                        firestore.collection("plants")
                            .add(updatedPlantData)
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
