package com.example.thesis.model.`object`

import android.util.Log
import com.example.thesis.model.data.MapMarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocalMarkerStore {
    private val _markers = MutableStateFlow<List<MapMarker>>(emptyList())
    val markersFlow: StateFlow<List<MapMarker>> = _markers.asStateFlow()

    fun addMarker(marker: MapMarker) {
        // Prevent duplicate IDs
        if (_markers.value.any { it.id == marker.id }) return
        _markers.value = _markers.value + marker
        Log.d("MarkerPopup", "LocalMarkerStore.addMarker — id=${marker.id} lat=${marker.latitude} lon=${marker.longitude} total=${_markers.value.size}")
    }

    fun setMarkers(markers: List<MapMarker>) {
        _markers.value = markers
    }

    fun getMarkers(): List<MapMarker> = _markers.value

    fun clear() {
        _markers.value = emptyList()
    }
}