package com.example.thesis.view.middleContent.parts.mapContent

import android.net.Uri
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.domain.repository.VideoRepository
import com.example.thesis.model.data.MapMarker
import com.example.thesis.model.data.VideoMarker
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.model.`object`.LocalMarkerStore
import com.example.thesis.model.`object`.LocalVideoMarkerStore
import com.example.thesis.viewmodel.middleContent.MapTrackingViewModel
import com.example.thesis.viewmodel.middleContent.MapUtils
import org.json.JSONObject

private const val TAG = "LocArcUI"

@Composable
fun MapViewScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    val trackingViewModel: MapTrackingViewModel = viewModel()
    val uiState by trackingViewModel.uiState.collectAsState()

    val map = remember { MapUtils.createMap(context) }
    val damageOverlay = remember { MapUtils.createGraphicsOverlayFromAssets(context) }
    val userOverlay = remember { GraphicsOverlay() }
    val photoMarkerOverlay = remember { GraphicsOverlay() }
    val videoMarkerOverlay = remember { GraphicsOverlay() }

    val photoMarkers by LocalMarkerStore.markersFlow.collectAsState()
    val videoMarkers by LocalVideoMarkerStore.videoMarkersFlow.collectAsState()
    
    var selectedMarker by remember { mutableStateOf<MapMarker?>(null) }

    val photoRepo = remember { PhotoRepository(context) }
    val videoRepo = remember { VideoRepository(context, "http://192.168.254.201:8080/api/upload/video") }

    // PERSISTENCE: Load existing detections from Gallery on startup
    LaunchedEffect(Unit) {
        Log.i(TAG, "MapViewScreen composed. Loading existing markers.")
        trackingViewModel.startTracking()

        // Load Photos
        val existingPhotos = photoRepo.getDamageImagesFromGallery()
        existingPhotos.forEach { item ->
            val lat = item.latitude
            val lon = item.longitude
            if (lat != null && lon != null) {
                var severity = 1
                try {
                    item.inferenceData?.let {
                        val json = JSONObject(it)
                        val count = json.optInt("count", 0)
                        severity = when {
                            count <= 2 -> 1
                            count <= 5 -> 2
                            else -> 3
                        }
                    }
                } catch (e: Exception) { Log.e(TAG, "Severity parse error: ${e.message}") }

                LocalMarkerStore.addMarker(
                    MapMarker(
                        id = item.displayName,
                        latitude = lat,
                        longitude = lon,
                        severity = severity,
                        damageType = "Road Damage",
                        imageUrl = item.uri.toString(),
                        capturedAt = item.dateAddedSeconds.toString()
                    )
                )
            }
        }

        // Load Videos
        val existingVideos = videoRepo.getDamageVideosFromGallery()
        existingVideos.forEach { item ->
            val sLat = item.startLatitude
            val sLon = item.startLongitude
            val eLat = item.endLatitude
            val eLon = item.endLongitude
            if (sLat != null && sLon != null && eLat != null && eLon != null) {
                LocalVideoMarkerStore.addVideoMarker(
                    VideoMarker(
                        id = item.displayName,
                        startLatitude = sLat,
                        startLongitude = sLon,
                        endLatitude = eLat,
                        endLongitude = eLon,
                        videoUrl = item.uri.toString(),
                        recordedAt = item.dateAddedSeconds?.toString() ?: ""
                    )
                )
            }
        }
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

    // Photo Overlays
    LaunchedEffect(photoMarkers) {
        photoMarkerOverlay.graphics.clear()
        photoMarkers.forEach { marker ->
            val point = Point(x = marker.longitude, y = marker.latitude, spatialReference = SpatialReference.wgs84())
            val color = when (marker.severity) {
                1 -> ArcColor.green
                2 -> ArcColor.fromRgba(255, 165, 0)
                else -> ArcColor.red
            }
            val symbol = SimpleMarkerSymbol(style = SimpleMarkerSymbolStyle.Circle, color = color, size = 12f).apply {
                outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
            }
            photoMarkerOverlay.graphics.add(Graphic(geometry = point, symbol = symbol).apply {
                attributes["markerId"] = marker.id
            })
        }
    }

    // Video Overlays
    LaunchedEffect(videoMarkers) {
        videoMarkerOverlay.graphics.clear()
        videoMarkers.forEach { marker ->
            val startPoint = Point(x = marker.startLongitude, y = marker.startLatitude, spatialReference = SpatialReference.wgs84())
            val endPoint = Point(x = marker.endLongitude, y = marker.endLatitude, spatialReference = SpatialReference.wgs84())

            videoMarkerOverlay.graphics.add(Graphic(
                geometry = startPoint,
                symbol = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle, ArcColor.green, 14f).apply {
                    outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
                }
            ).apply { attributes["videoUrl"] = marker.videoUrl ?: "" })

            videoMarkerOverlay.graphics.add(Graphic(
                geometry = endPoint,
                symbol = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle, ArcColor.red, 14f).apply {
                    outline = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255), 2f)
                }
            ).apply { attributes["videoUrl"] = marker.videoUrl ?: "" })

            videoMarkerOverlay.graphics.add(Graphic(
                geometry = Polyline(listOf(startPoint, endPoint)),
                symbol = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, ArcColor.fromRgba(0, 0, 255, 128), 6f)
            ).apply { attributes["videoUrl"] = marker.videoUrl ?: "" })
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapView(
            modifier = Modifier.fillMaxSize(),
            arcGISMap = map,
            graphicsOverlays = listOf(damageOverlay, userOverlay, photoMarkerOverlay, videoMarkerOverlay),
            onSingleTapConfirmed = { tapEvent ->
                val tapPoint = tapEvent.mapPoint ?: return@MapView
                val wgs84 = SpatialReference.wgs84()
                val tapWgs84 = GeometryEngine.projectOrNull(tapPoint, wgs84) as? Point ?: return@MapView

                val tappedVideo = videoMarkerOverlay.graphics.firstOrNull { graphic ->
                    val geom = graphic.geometry
                    val dist = when (geom) {
                        is Point -> Math.sqrt(Math.pow(geom.x - tapWgs84.x, 2.0) + Math.pow(geom.y - tapWgs84.y, 2.0))
                        else -> 0.0005 // Simple buffer for lines
                    }
                    dist < 0.0006
                }

                if (tappedVideo != null) {
                    val url = tappedVideo.attributes["videoUrl"] as? String
                    if (!url.isNullOrBlank()) {
                        navController.navigate("${NAVIGATIONPATH.VIDEO_DAMAGE.route}/${Uri.encode(url)}")
                    }
                    return@MapView
                }

                val tappedPhoto = photoMarkerOverlay.graphics
                    .mapNotNull { graphic ->
                        val geom = graphic.geometry as? Point ?: return@mapNotNull null
                        val dist = Math.sqrt(Math.pow(geom.x - tapWgs84.x, 2.0) + Math.pow(geom.y - tapWgs84.y, 2.0))
                        if (dist < 0.0006) graphic to dist else null
                    }
                    .minByOrNull { it.second }?.first

                if (tappedPhoto != null) {
                    val markerId = tappedPhoto.attributes["markerId"] as? String
                    selectedMarker = photoMarkers.firstOrNull { it.id == markerId }
                } else {
                    selectedMarker = null
                }
            }
        )

        selectedMarker?.let { marker ->
            MarkerPopup(marker = marker, onDismiss = { selectedMarker = null }, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
        }
    }
}

@Composable
fun MarkerPopup(marker: MapMarker, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close", tint = Color.Gray) }
            }
            if (!marker.imageUrl.isNullOrBlank()) {
                AsyncImage(model = marker.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)))
            }
            Spacer(modifier = Modifier.height(8.dp))
            val (label, color) = when (marker.severity) {
                1 -> "Low" to Color(0xFF4CAF50)
                2 -> "Medium" to Color(0xFFFF9800)
                else -> "High" to Color(0xFFF44336)
            }
            Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.15f)) {
                Text(text = "Severity: $label", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = color, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "📍 ${String.format("%.5f", marker.latitude)}, ${String.format("%.5f", marker.longitude)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
