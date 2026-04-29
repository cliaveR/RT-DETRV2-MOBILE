package com.example.thesis.model.`object`

import com.example.thesis.model.data.VideoMarker

object LocalVideoMarkerStore {
    private val videoMarkers = mutableListOf<VideoMarker>()

    fun addVideoMarker(marker: VideoMarker) {
        videoMarkers.add(marker)
    }

    fun getVideoMarkers(): List<VideoMarker> {
        return videoMarkers.toList()
    }

    fun clear() {
        videoMarkers.clear()
    }
}

