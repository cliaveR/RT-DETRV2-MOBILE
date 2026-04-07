package com.example.thesis.model.data

data class RoadDamage(
    val longitude: Double,
    val latitude: Double,
    val type: String, // e.g., "Pothole", "Crack"
    val severity: Int // 1, 2, or 3
)
