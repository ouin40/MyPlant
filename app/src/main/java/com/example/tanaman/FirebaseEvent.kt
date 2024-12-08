package com.example.tanaman

data class FirebaseEvent(
    val tasks: List<WateringTask> = emptyList()
)
