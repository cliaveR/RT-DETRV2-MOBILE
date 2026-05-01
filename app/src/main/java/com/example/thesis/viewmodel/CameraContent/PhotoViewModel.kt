package com.example.thesis.viewmodel.CameraContent

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.camera.CameraManager
import com.example.thesis.domain.location.CurrentLocationProvider
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.model.data.MapMarker
import com.example.thesis.model.`object`.LocalMarkerStore
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    var isUploading by mutableStateOf(false)
        private set

    private var repository: PhotoRepository? = null

    private companion object {
        const val TAG = "PhotoVM"
    }

    private fun getRepository(context: Context): PhotoRepository {
        if (repository == null) {
            repository = PhotoRepository(context)
        }
        return repository!!
    }

    fun captureAndUpload(
        context: Context,
        cameraManager: CameraManager,
        onResult: (String) -> Unit
    ) {
        val repo = getRepository(context)
        val locationProvider = CurrentLocationProvider(context)

        cameraManager.takePhoto { uri ->
            if (uri != null) {
                viewModelScope.launch {
                    isUploading = true
                    try {
                        val captureCoordinate = locationProvider.getCurrentCoordinateOrNull()
                        val result = repo.uploadAndSaveVisualized(
                            uri = uri,
                            captureCoordinate = captureCoordinate
                        )

                        if (result != null) {
                            val markerLat = captureCoordinate?.latitude ?: result.latitude
                            val markerLon = captureCoordinate?.longitude ?: result.longitude

                            if (markerLat != null && markerLon != null) {
                                val inferenceJson = result.inferenceData?.let { org.json.JSONObject(it) }
                                val detections = inferenceJson?.optJSONArray("detections")
                                val firstDetection = detections?.optJSONObject(0)

                                val damageType = firstDetection?.optString("class", "Unknown") ?: "Unknown"
                                val confidence = firstDetection?.optDouble("confidence", 0.0) ?: 0.0
                                val severity = when {
                                    confidence >= 0.75 -> 3
                                    confidence >= 0.50 -> 2
                                    else -> 1
                                }

                                // Use the filename as the unique ID for consistency with persistence
                                val markerId = result.savedImageUri?.lastPathSegment ?: System.currentTimeMillis().toString()

                                LocalMarkerStore.addMarker(
                                    MapMarker(
                                        id = markerId,
                                        longitude = markerLon,
                                        latitude = markerLat,
                                        severity = severity,
                                        damageType = damageType,
                                        imageUrl = result.savedImageUri?.toString(),
                                        capturedAt = java.time.Instant.now().toString()
                                    )
                                )
                            }
                            onResult("Processed image saved to Gallery ✅")
                        } else {
                            onResult("Upload/processing failed ❌")
                        }
                    } catch (e: Exception) {
                        onResult("Error: ${e.message}")
                    }
                    isUploading = false
                }
            } else {
                onResult("Capture failed ❌")
            }
        }
    }
}
