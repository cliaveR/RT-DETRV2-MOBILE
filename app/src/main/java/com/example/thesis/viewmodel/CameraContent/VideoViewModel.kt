package com.example.thesis.viewmodel.CameraContent

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.camera.CameraManager
import com.example.thesis.domain.location.CurrentLocationProvider
import com.example.thesis.domain.repository.VideoRepository
import com.example.thesis.model.data.VideoMarker
import com.example.thesis.model.data.mapTracking.GeoCoordinate
import com.example.thesis.model.data.mapTracking.VideoCaptureCoordinates
import com.example.thesis.model.`object`.LocalVideoMarkerStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class VideoViewModel(
    private val repository: VideoRepository,
    private val locationProvider: CurrentLocationProvider
) : ViewModel() {

    var isRecording by mutableStateOf(false)
    var isProcessing by mutableStateOf(false)

    var processedVideoUri by mutableStateOf<Uri?>(null)
    var processingTimeMs by mutableStateOf<Long?>(null)

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private var recordingStartCoordinate: GeoCoordinate? = null
    private var recordingEndCoordinate: GeoCoordinate? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording(cameraManager: CameraManager) {
        if (isRecording) {
            viewModelScope.launch {
                // Capture end position exactly when button is pressed
                recordingEndCoordinate = locationProvider.getCurrentCoordinateOrNull()
                Log.d("VideoMarker", "Recording STOPPED. End Coords: $recordingEndCoordinate")
                cameraManager.stopRecording()
                isRecording = false
            }
        } else {
            viewModelScope.launch {
                // Capture start position
                recordingStartCoordinate = locationProvider.getCurrentCoordinateOrNull()
                Log.d("VideoMarker", "Recording STARTED. Start Coords: $recordingStartCoordinate")
                recordingEndCoordinate = null

                cameraManager.startRecording { uri ->
                    uri?.let {
                        Log.d("VideoMarker", "Video file saved locally, starting upload/analysis...")
                        isProcessing = true
                        val startTime = System.currentTimeMillis()

                        val metadata = VideoCaptureCoordinates(
                            start = recordingStartCoordinate,
                            end = recordingEndCoordinate
                        )

                        repository.uploadVideo(it, metadata) { success, resultUri ->
                            val endTime = System.currentTimeMillis()
                            processingTimeMs = endTime - startTime
                            isProcessing = false

                            viewModelScope.launch {
                                if (success && resultUri != null) {
                                    Log.d("VideoMarker", "Analysis Success. URI: $resultUri")
                                    processedVideoUri = resultUri
                                    _toastMessage.emit("Video analysis successful! ✅")

                                    // PERSISTENCE FIX: Fetch video details to use the filename as ID
                                    val videoDetails = repository.getVideoDetails(resultUri)
                                    val markerId = videoDetails?.displayName ?: UUID.randomUUID().toString()

                                    if (recordingStartCoordinate != null && recordingEndCoordinate != null) {
                                        var endLat = recordingEndCoordinate!!.latitude
                                        var endLon = recordingEndCoordinate!!.longitude

                                        // TEST OFFSET: Ensure a line is visible even if the user is stationary
                                        if (endLat == recordingStartCoordinate!!.latitude && endLon == recordingStartCoordinate!!.longitude) {
                                            endLat += 0.0001
                                            endLon += 0.0001
                                        }

                                        val marker = VideoMarker(
                                            id = markerId,
                                            startLatitude = recordingStartCoordinate!!.latitude,
                                            startLongitude = recordingStartCoordinate!!.longitude,
                                            endLatitude = endLat,
                                            endLongitude = endLon,
                                            videoUrl = resultUri.toString(),
                                            recordedAt = java.time.Instant.now().toString()
                                        )
                                        LocalVideoMarkerStore.addVideoMarker(marker)
                                        Log.d("VideoMarker", "Marker ADDED with persistent ID: ${marker.id}")
                                    }
                                } else {
                                    Log.e("VideoMarker", "Analysis FAILED")
                                    _toastMessage.emit("Video analysis failed. ❌")
                                }
                                
                                // Reset for next session
                                recordingStartCoordinate = null
                                recordingEndCoordinate = null
                            }
                        }
                    }
                }
                isRecording = true
            }
        }
    }
}

class VideoViewModelFactory(
    private val repository: VideoRepository,
    private val locationProvider: CurrentLocationProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            return VideoViewModel(repository, locationProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
