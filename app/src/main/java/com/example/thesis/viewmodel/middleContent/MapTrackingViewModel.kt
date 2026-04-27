package com.example.thesis.viewmodel.middleContent

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.location.FusedLocationTrackingService
import com.example.thesis.model.data.mapTracking.MapTrackingUiState
import com.example.thesis.model.data.mapTracking.TrackedLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapTrackingViewModel(application: Application) : AndroidViewModel(application) {

    private companion object {
        const val TAG = "LocVM"
    }

    private val trackingService = FusedLocationTrackingService(application)

    private val _uiState = MutableStateFlow(MapTrackingUiState())
    val uiState: StateFlow<MapTrackingUiState> = _uiState

    private var trackingJob: Job? = null

    fun startTracking() {
        if (trackingJob != null) return

        Log.i(TAG, "startTracking() called")
        _uiState.update { it.copy(isTracking = true, errorMessage = null) }

        trackingJob = viewModelScope.launch {
            trackingService.locationUpdates()
                .catch { e ->
                    Log.e(TAG, "Location flow error: ${e.message}", e)
                    _uiState.update {
                        it.copy(
                            isTracking = false,
                            errorMessage = e.message ?: "Failed to receive location updates."
                        )
                    }
                }
                .collect { location ->
                    Log.d(
                        TAG,
                        "collect location lat=${location.latitude}, lng=${location.longitude}, " +
                                "acc=${location.accuracyMeters}m, seqWillIncrement=true"
                    )
                    _uiState.update { current ->
                        current.copy(
                            currentLocation = location,
                            cameraTarget = location,
                            cameraMoveSequence = current.cameraMoveSequence + 1
                        )
                    }
                }
        }
    }

    fun stopTracking() {
        Log.i(TAG, "stopTracking() called")
        trackingJob?.cancel()
        trackingJob = null
        _uiState.update { it.copy(isTracking = false) }
    }

    fun centerOnUser() {
        val current = _uiState.value.currentLocation ?: return
        Log.d(TAG, "centerOnUser() current=$current")
        bumpCameraTarget(current)
    }

    private fun bumpCameraTarget(target: TrackedLocation) {
        _uiState.update { current ->
            current.copy(
                cameraTarget = target,
                cameraMoveSequence = current.cameraMoveSequence + 1
            )
        }
    }

    override fun onCleared() {
        stopTracking()
        super.onCleared()
    }
}