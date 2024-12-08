package com.example.tanaman

data class WateringTask(
    val plantName: String = "",
    val waterQuantity: String = "",
    val imageResource: Int = 0,
    val actionIconResource: Int = 0,
    var isDone: Boolean = false
)
