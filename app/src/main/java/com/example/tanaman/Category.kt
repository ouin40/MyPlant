package com.example.tanaman

import android.graphics.Bitmap

data class Category(
    val name: String = "",
    val plants: List<Plant> = emptyList() // Make sure plants are of type List<Plant>
)
