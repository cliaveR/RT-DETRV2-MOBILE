package com.example.thesis.view.middleContent.parts.mapContent

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.Color as ArcColor
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.symbology.SimpleLineSymbol
import com.arcgismaps.mapping.symbology.SimpleLineSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.viewmodel.middleContent.MapTrackingViewModel
import com.example.thesis.viewmodel.middleContent.MapUtils
import com.example.thesis.model.`object`.LocalMarkerStore
import com.example.thesis.model.`object`.LocalVideoMarkerStore
import com.arcgismaps.geometry.Polyline

private const val TAG = "LocArcUI"

@Composable
fun MapViewScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val trackingViewModel: MapTrackingViewModel = viewModel()
    val uiState by trackingViewModel.uiState.collectAsState()

    val map = remember { MapUtils.createMap(context) }
    val damageOverlay = remember { MapUtils.createGraphicsOverlayFromAssets(context) }
    val userOverlay = remember { GraphicsOverlay() }
    val photoMarkerOverlay = remember { GraphicsOverlay() }
    val videoMarkerOverlay = remember { GraphicsOverlay() }

    var photoMarkers by remember { mutableStateOf(LocalMarkerStore.getMarkers()) }
    var videoMarkers by remember { mutableStateOf(LocalVideoMarkerStore.getVideoMarkers()) }

    LaunchedEffect(Unit) {
        Log.i(TAG, "MapViewScreen composed. Starting tracking.")
        trackingViewModel.startTracking()
        photoMarkers = LocalMarkerStore.getMarkers()
        videoMarkers = LocalVideoMarkerStore.getVideoMarkers()
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.i(TAG, "MapViewScreen disposed. Stopping tracking.")
            trackingViewModel.stopTracking()
        }
    }

    LaunchedEffect(uiState.cameraMoveSequence) {
        val target = uiState.cameraTarget ?: return@LaunchedEffect

        userOverlay.graphics.clear()
        userOverlay.graphics.add(
            MapUtils.createPointGraphic(
                longitude = target.longitude,
                latitude = target.latitude,
                color = ArcColor.green
            )
        )

        map.initialViewpoint = Viewpoint(
            latitude = target.latitude,
            longitude = target.longitude,
            scale = 2500.0
        )
    }

    LaunchedEffect(photoMarkers) {
        photoMarkerOverlay.graphics.clear()
        photoMarkers.forEach { marker ->
            val point = Point(
                x = marker.longitude,
                y = marker.latitude,
                spatialReference = SpatialReference.wgs84()
            )

            val color = when (marker.severity) {
                1 -> ArcColor.green
                2 -> ArcColor.fromRgba(255, 165, 0)
                else -> ArcColor.red
            }

            val symbol = SimpleMarkerSymbol(
                style = SimpleMarkerSymbolStyle.Circle,
                color = color,
                size = 12f
            ).apply {
                outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
            }

            photoMarkerOverlay.graphics.add(Graphic(geometry = point, symbol = symbol))
        }
    }

    LaunchedEffect(videoMarkers) {
        videoMarkerOverlay.graphics.clear()
        videoMarkers.forEach { marker ->
            val startPoint = Point(
                x = marker.startLongitude,
                y = marker.startLatitude,
                spatialReference = SpatialReference.wgs84()
            )
            val startSymbol = SimpleMarkerSymbol(
                style = SimpleMarkerSymbolStyle.Circle,
                color = ArcColor.green,
                size = 14f
            ).apply {
                outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
            }
            videoMarkerOverlay.graphics.add(Graphic(geometry = startPoint, symbol = startSymbol))

            val endPoint = Point(
                x = marker.endLongitude,
                y = marker.endLatitude,
                spatialReference = SpatialReference.wgs84()
            )
            val endSymbol = SimpleMarkerSymbol(
                style = SimpleMarkerSymbolStyle.Circle,
                color = ArcColor.red,
                size = 14f
            ).apply {
                outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
            }
            videoMarkerOverlay.graphics.add(Graphic(geometry = endPoint, symbol = endSymbol))

            val line = Polyline(listOf(startPoint, endPoint))
            val lineSymbol = SimpleLineSymbol(
                SimpleLineSymbolStyle.Solid,
                ArcColor.fromRgba(0, 0, 255, 128),
                2f
            )
            videoMarkerOverlay.graphics.add(Graphic(geometry = line, symbol = lineSymbol))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapView(
            modifier = Modifier.fillMaxSize(),
            arcGISMap = map,
            graphicsOverlays = listOf(damageOverlay, userOverlay, photoMarkerOverlay, videoMarkerOverlay)
        )
    }
}
