package com.example.tanaman

import android.graphics.Bitmap

data class Category(
    val name: String,
    var plants: MutableList<Bitmap> // Ubah menjadi MutableList agar dapat diubah
)
