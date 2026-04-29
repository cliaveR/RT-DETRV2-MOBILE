package com.example.thesis.model.data

data class VideoMarker(
    val id: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val severity: Int = 2,
    val damageType: String = "Video Survey",
    val videoUrl: String? = null,
    val recordedAt: String,
    val durationSeconds: Long = 0
)

