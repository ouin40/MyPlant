package com.example.tanaman

import android.graphics.Bitmap

data class Category(
    val name: String,
    val plants: MutableList<Pair<String, Bitmap>> // Pasangan nama dan gambar
)
