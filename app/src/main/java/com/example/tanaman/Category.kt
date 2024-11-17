package com.example.tanaman

import android.graphics.Bitmap

data class Category(
    val name: String, // Nama kategori
    val plants: List<Bitmap> // Gambar tanaman dalam kategori
)
