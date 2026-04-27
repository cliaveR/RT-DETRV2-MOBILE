package com.example.thesis.model.data.mapTracking

data class TrackedLocation(
    val latitude: Double,
    val longitude: Double,
    val timeStampMs: Long = System.currentTimeMillis( ),
    val accuracyMeters: Float? = null,
)