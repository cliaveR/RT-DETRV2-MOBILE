package com.example.thesis.model.data.mapTracking

data class MapTrackingUiState (
    val isTracking: Boolean = false,
    val currentLocation: TrackedLocation? = null,
    val cameraTarget : TrackedLocation? = null,
    val cameraMoveSequence: Long = 0L,
    val errorMessage : String? = null
)