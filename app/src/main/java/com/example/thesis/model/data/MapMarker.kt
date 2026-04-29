package com.example.thesis.model.data

data class MapMarker(
    val id: String,
    val longitude: Double,
    val latitude: Double,
    val severity: Int = 2,
    val damageType: String = "Unknown",
    val imageUrl: String? = null,
    val capturedAt: String? = null
)

