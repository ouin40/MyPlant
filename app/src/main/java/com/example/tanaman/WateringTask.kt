package com.example.tanaman

data class WateringTask(
    val plantName: String,
    val waterQuantity: String,
    val imageResource: Int,
    val actionIconResource: Int,
    var isDone: Boolean = false // Default value is false
)
