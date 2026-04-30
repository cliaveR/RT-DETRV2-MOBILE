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
import com.example.thesis.model.data.mapTracking.GeoCoordinate
import com.example.thesis.model.data.mapTracking.VideoCaptureCoordinates
import kotlinx.coroutines.launch

class VideoViewModel(
    private val repository: VideoRepository,
    private val locationProvider: CurrentLocationProvider
) : ViewModel() {

    var isRecording by mutableStateOf(false)
    var isProcessing by mutableStateOf(false)

    var processedVideoUri by mutableStateOf<Uri?>(null)
    var processingTimeMs by mutableStateOf<Long?>(null)

    private var recordingStartCoordinate: GeoCoordinate? = null
    private var recordingEndCoordinate: GeoCoordinate? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording(cameraManager: CameraManager) {
        if (isRecording) {
            viewModelScope.launch {
                recordingEndCoordinate = locationProvider.getCurrentCoordinateOrNull()
                cameraManager.stopRecording()
                isRecording = false
            }
        } else {
            viewModelScope.launch {
                recordingStartCoordinate = locationProvider.getCurrentCoordinateOrNull()
                recordingEndCoordinate = null

                cameraManager.startRecording { uri ->
                    uri?.let {

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

                            if (success) {
                                processedVideoUri = resultUri
                            }

                            recordingStartCoordinate = null
                            recordingEndCoordinate = null
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
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoViewModel(repository, locationProvider) as T
    }
}
