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
                        // Capture coordinate immediately before upload request.
                        val captureCoordinate = locationProvider.getCurrentCoordinateOrNull()
                        Log.d(TAG, "Photo capture coordinate=$captureCoordinate")

                        val savedUri = repo.uploadAndSaveVisualized(
                            uri = uri,
                            captureCoordinate = captureCoordinate
                        )

                        onResult(
                            if (savedUri != null) {
                                "Processed image saved to Gallery ✅"
                            } else {
                                "Upload/processing failed ❌"
                            }
                        )
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