package com.example.tanaman

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Plant_Storage : Fragment() {
    private lateinit var addPlantButton: Button
    private lateinit var recyclerView: RecyclerView
    private val imageList = ArrayList<Bitmap>()
    private lateinit var adapter: PlantImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_storage, container, false)

        addPlantButton = view.findViewById(R.id.addPlant)
        recyclerView = view.findViewById(R.id.image_recycler_view)

        // Set up RecyclerView with adapter
        adapter = PlantImageAdapter(imageList)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        // Add plant button opens the camera
        addPlantButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }

        // Test with a sample image if available
        val sampleBitmap = BitmapFactory.decodeResource(resources, R.drawable.plant1)
        imageList.add(sampleBitmap)
        adapter.notifyDataSetChanged()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                imageList.add(imageBitmap)
                adapter.notifyDataSetChanged()
                Toast.makeText(context, "Image added to list", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No image captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
