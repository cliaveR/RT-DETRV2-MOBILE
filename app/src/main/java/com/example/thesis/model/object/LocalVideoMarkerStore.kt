package com.example.thesis.model.`object`

import com.example.thesis.model.data.VideoMarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocalVideoMarkerStore {
    private val _videoMarkers = MutableStateFlow<List<VideoMarker>>(emptyList())
    val videoMarkersFlow: StateFlow<List<VideoMarker>> = _videoMarkers.asStateFlow()

    fun addVideoMarker(marker: VideoMarker) {
        if (_videoMarkers.value.any { it.id == marker.id }) return
        _videoMarkers.value = _videoMarkers.value + marker
    }

    fun setVideoMarkers(markers: List<VideoMarker>) {
        _videoMarkers.value = markers
    }

    fun getVideoMarkers(): List<VideoMarker> {
        return _videoMarkers.value
    }

    fun clear() {
        _videoMarkers.value = emptyList()
    }
}
