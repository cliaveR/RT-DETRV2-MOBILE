package com.example.thesis.domain.location

import com.example.thesis.model.data.mapTracking.TrackedLocation
import kotlinx.coroutines.flow.Flow

interface LocationTrackingService {
    fun locationUpdates(intervalMs: Long=1500L): Flow<TrackedLocation>
}