package com.example.tanaman

import android.graphics.Bitmap

data class Plant(
    val name: String,
    val description: String,
    val lightLevel: Int,
    val temperature: String,
    val wateringFrequency: String,
    val imageUrl: String? // Add a URL for the image
)

