package com.example.thesis.viewmodel.CameraContent;

import android.Manifest
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thesis.domain.camera.CameraManager
import java.net.URI
import com.example.thesis.domain.repository.VideoRepository
import java.io.File

class VideoViewModel(private val repository: VideoRepository) : ViewModel() {
    var isRecording by mutableStateOf(false)
    var isProcessing by mutableStateOf(false)
    var processedVideoFile by mutableStateOf<File?>(null)

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording(cameraManager: CameraManager) {
        if (isRecording) {
            cameraManager.stopRecording()
        } else {
            cameraManager.startRecording { uri ->
                uri?.let {
                    isProcessing = true
                    repository.uploadVideo(it) { success, resultFile ->
                        isProcessing = false
                        if (success) {
                            processedVideoFile = resultFile
                        }
                    }
                }
            }
        }
        isRecording = !isRecording
    }
}

class VideoViewModelFactory(private val repository: VideoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoViewModel(repository) as T
    }
}