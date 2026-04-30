package com.example.thesis.view.middleContent.parts.mapContent

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.arcgismaps.Color as ArcColor
import com.arcgismaps.geometry.GeometryEngine
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.Polyline
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.symbology.*
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.model.data.MapMarker
import com.example.thesis.model.`object`.LocalMarkerStore
import com.example.thesis.model.`object`.LocalVideoMarkerStore
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
    val photoMarkerOverlay = remember { GraphicsOverlay() }
    val videoMarkerOverlay = remember { GraphicsOverlay() }

    // Reactive — updates automatically when addMarker() is called
    val photoMarkers by LocalMarkerStore.markersFlow.collectAsState()
    var videoMarkers by remember { mutableStateOf(LocalVideoMarkerStore.getVideoMarkers()) }
    var selectedMarker by remember { mutableStateOf<MapMarker?>(null) }

    LaunchedEffect(Unit) {
        Log.i(TAG, "MapViewScreen composed. Starting tracking.")
        trackingViewModel.startTracking()
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

    // Rebuilds overlay whenever photoMarkers changes — stores marker.id in attribute
    LaunchedEffect(photoMarkers) {
        Log.d("MarkerPopup", "LaunchedEffect(photoMarkers) — rebuilding overlay, count=${photoMarkers.size}")
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
            // Store marker.id instead of index — always stable regardless of list changes
            val graphic = Graphic(geometry = point, symbol = symbol).apply {
                attributes["markerId"] = marker.id
            }
            photoMarkerOverlay.graphics.add(graphic)
            Log.d("MarkerPopup", "Added graphic markerId=${marker.id} lat=${marker.latitude} lon=${marker.longitude}")
        }
    }

    LaunchedEffect(videoMarkers) {
        videoMarkerOverlay.graphics.clear()
        videoMarkers.forEach { marker ->
            val startPoint = Point(x = marker.startLongitude, y = marker.startLatitude, spatialReference = SpatialReference.wgs84())
            val startSymbol = SimpleMarkerSymbol(style = SimpleMarkerSymbolStyle.Circle, color = ArcColor.green, size = 14f).apply {
                outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
            }
            videoMarkerOverlay.graphics.add(Graphic(geometry = startPoint, symbol = startSymbol))

            val endPoint = Point(x = marker.endLongitude, y = marker.endLatitude, spatialReference = SpatialReference.wgs84())
            val endSymbol = SimpleMarkerSymbol(style = SimpleMarkerSymbolStyle.Circle, color = ArcColor.red, size = 14f).apply {
                outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
            }
            videoMarkerOverlay.graphics.add(Graphic(geometry = endPoint, symbol = endSymbol))

            val line = Polyline(listOf(startPoint, endPoint))
            val lineSymbol = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255, 128), 2f)
            videoMarkerOverlay.graphics.add(Graphic(geometry = line, symbol = lineSymbol))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapView(
            modifier = Modifier.fillMaxSize(),
            arcGISMap = map,
            graphicsOverlays = listOf(damageOverlay, userOverlay, photoMarkerOverlay, videoMarkerOverlay),
            onSingleTapConfirmed = { tapEvent ->
                Log.d("MarkerPopup", "Overlay graphic count=${photoMarkerOverlay.graphics.size}")
                Log.d("MarkerPopup", "LocalMarkerStore count=${LocalMarkerStore.getMarkers().size}")

                val tapPoint = tapEvent.mapPoint ?: return@MapView
                val wgs84 = SpatialReference.wgs84()
                val tapWgs84 = GeometryEngine.projectOrNull(tapPoint, wgs84) as? Point

                Log.d("MarkerPopup", "Tap WGS84=(${tapWgs84?.x}, ${tapWgs84?.y})")

                if (tapWgs84 == null) {
                    Log.w("MarkerPopup", "Projection failed")
                    return@MapView
                }

                val tappedGraphics = photoMarkerOverlay.graphics.filter { graphic ->
                    val geom = graphic.geometry as? Point ?: return@filter false
                    val dx = geom.x - tapWgs84.x
                    val dy = geom.y - tapWgs84.y
                    val dist = Math.sqrt(dx * dx + dy * dy)
                    Log.d("MarkerPopup", "  checking markerId=${graphic.attributes["markerId"]} dist=$dist")
                    dist < 0.001
                }

                Log.d("MarkerPopup", "Hit test — hits=${tappedGraphics.size}")

                val tapped = tappedGraphics.firstOrNull()
                if (tapped != null) {
                    val markerId = tapped.attributes["markerId"] as? String
                    // Look up from LIVE photoMarkers StateFlow — always the latest data
                    selectedMarker = photoMarkers.firstOrNull { it.id == markerId }
                    Log.d("MarkerPopup", if (selectedMarker != null)
                        "Showing popup — markerId=$markerId imageUrl=${selectedMarker!!.imageUrl}"
                    else
                        "markerId=$markerId not found in live list (size=${photoMarkers.size})")
                } else {
                    selectedMarker = null
                    Log.d("MarkerPopup", "No marker hit — dismissing popup")
                }
            }
        )

        selectedMarker?.let { marker ->
            MarkerPopup(
                marker = marker,
                onDismiss = { selectedMarker = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun MarkerPopup(
    marker: MapMarker,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!marker.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = marker.imageUrl,
                    contentDescription = "Damage photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No image available", color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val (severityLabel, severityColor) = when (marker.severity) {
                1 -> "Low" to Color(0xFF4CAF50)
                2 -> "Medium" to Color(0xFFFF9800)
                else -> "High" to Color(0xFFF44336)
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = severityColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Severity: $severityLabel",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = severityColor,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "📍 ${String.format("%.5f", marker.latitude)}, ${String.format("%.5f", marker.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            marker.capturedAt?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🕒 $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}