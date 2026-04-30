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
import java.util.UUID

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
                        Log.d(TAG, "Photo capture coordinate=$captureCoordinate")

                        val result = repo.uploadAndSaveVisualized(
                            uri = uri,
                            captureCoordinate = captureCoordinate
                        )

                        Log.d(TAG, "uploadAndSaveVisualized result=$result")

                        if (result != null) {
                            // Prefer GPS coordinate, fall back to server-echoed coords
                            val markerLat = captureCoordinate?.latitude ?: result.latitude
                            val markerLon = captureCoordinate?.longitude ?: result.longitude

                            Log.d("MarkerPopup", "Upload success — lat=$markerLat lon=$markerLon imageUri=${result.savedImageUri}")

                            if (markerLat != null && markerLon != null) {
                                // Parse inferenceData JSON from server
                                val inferenceJson = result.inferenceData?.let { org.json.JSONObject(it) }
                                val detections = inferenceJson?.optJSONArray("detections")
                                val firstDetection = detections?.optJSONObject(0)

                                val damageType = firstDetection?.optString("class", "Unknown") ?: "Unknown"
                                val confidence = firstDetection?.optDouble("confidence", 0.0) ?: 0.0
                                val severity = when {
                                    confidence >= 0.75 -> 3  // High
                                    confidence >= 0.50 -> 2  // Medium
                                    else -> 1                // Low
                                }

                                Log.d("MarkerPopup", "Parsed — damageType=$damageType confidence=$confidence severity=$severity")

                                // USE A TRULY UNIQUE ID AND ENSURE savedImageUri IS USED
                                val uniqueId = UUID.randomUUID().toString()

                                LocalMarkerStore.addMarker(
                                    MapMarker(
                                        id = uniqueId,
                                        longitude = markerLon,
                                        latitude = markerLat,
                                        severity = severity,
                                        damageType = damageType,
                                        imageUrl = result.savedImageUri?.toString(),
                                        capturedAt = java.time.Instant.now().toString()
                                    )
                                )
                                Log.d("MarkerPopup", "Marker added! Store size=${LocalMarkerStore.getMarkers().size}")
                            } else {
                                Log.w("MarkerPopup", "No coords from GPS or server — marker skipped")
                            }

                            onResult("Processed image saved to Gallery ✅")
                        } else {
                            Log.w("MarkerPopup", "Upload returned null — marker not added")
                            onResult("Upload/processing failed ❌")
                        }
                    } catch (e: Exception) {
                        Log.e("MarkerPopup", "captureAndUpload exception: ${e.message}")
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
