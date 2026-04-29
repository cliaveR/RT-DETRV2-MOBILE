package com.example.thesis.model.`object`

import com.example.thesis.model.data.MapMarker
import com.arcgismaps.Color as ArcColor

/**
 * Simple in-memory store for markers created from photo uploads.
 * This avoids needing a separate backend endpoint — markers are extracted
 * from the photo upload response and stored locally.
 */
object LocalMarkerStore {
    private val markers = mutableListOf<MapMarker>()

    fun addMarker(marker: MapMarker) {
        markers.add(marker)
    }

    fun getMarkers(): List<MapMarker> = markers.toList()

    fun clear() {
        markers.clear()
    }
}

