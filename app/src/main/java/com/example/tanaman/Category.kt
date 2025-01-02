package com.example.tanaman

import android.graphics.Bitmap

data class Category(
    val name: String,
    val plants: MutableList<Triple<String, String, Bitmap>> // Triple (plantId, plantName, plantImage)
)
