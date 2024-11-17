package com.example.tanaman

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Plant_Storage : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addPlantButton: Button
    private val categories = arrayListOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_storage, container, false)

        recyclerView = view.findViewById(R.id.category_recycler_view)
        addPlantButton = view.findViewById(R.id.addPlant)

        // Dummy Data untuk kategori dan tanaman
        val samplePlants1 = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.plant1),
            BitmapFactory.decodeResource(resources, R.drawable.plant2),
            BitmapFactory.decodeResource(resources, R.drawable.plant3)
        )
        val samplePlants2 = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.plant4),
            BitmapFactory.decodeResource(resources, R.drawable.plant5),
            BitmapFactory.decodeResource(resources, R.drawable.plant6)
        )

        categories.add(Category("Dapur", samplePlants1))
        categories.add(Category("Ruang Tamu", samplePlants2))

        // Set up RecyclerView untuk kategori
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CategoryAdapter(categories)

        addPlantButton.setOnClickListener {
            Toast.makeText(context, "Add Plant button clicked!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
