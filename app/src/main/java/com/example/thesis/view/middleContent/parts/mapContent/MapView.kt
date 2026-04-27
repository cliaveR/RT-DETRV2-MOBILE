package com.example.thesis.view.middleContent.parts.mapContent

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.Color as ArcColor
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.viewmodel.middleContent.MapTrackingViewModel
import com.example.thesis.viewmodel.middleContent.MapUtils

private const val TAG = "LocArcUI"

@Composable
fun MapViewScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val trackingViewModel: MapTrackingViewModel = viewModel()
    val uiState by trackingViewModel.uiState.collectAsState()

    val map = remember { MapUtils.createMap(context) }
    val damageOverlay = remember { MapUtils.createGraphicsOverlayFromAssets(context) }
    val userOverlay = remember { GraphicsOverlay() }

    LaunchedEffect(Unit) {
        Log.i(TAG, "MapViewScreen composed. Starting tracking.")
        trackingViewModel.startTracking()
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.i(TAG, "MapViewScreen disposed. Stopping tracking.")
            trackingViewModel.stopTracking()
        }
    }

    LaunchedEffect(uiState.cameraMoveSequence) {
        val target = uiState.cameraTarget ?: return@LaunchedEffect

        Log.d(
            TAG,
            "Update ArcGIS marker/camera lat=${target.latitude}, lng=${target.longitude}, seq=${uiState.cameraMoveSequence}"
        )

        userOverlay.graphics.clear()
        userOverlay.graphics.add(
            MapUtils.createPointGraphic(
                longitude = target.longitude,
                latitude = target.latitude,
                color = ArcColor.green
            )
        )

        // Recenter map to live user location.
        map.initialViewpoint = Viewpoint(
            latitude = target.latitude,
            longitude = target.longitude,
            scale = 2500.0
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapView(
            modifier = Modifier.fillMaxSize(),
            arcGISMap = map,
            graphicsOverlays = listOf(damageOverlay, userOverlay)
        )
    }
}