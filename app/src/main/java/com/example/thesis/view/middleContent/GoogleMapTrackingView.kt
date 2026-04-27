package com.example.thesis.view.middleContent.parts.mapContent

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesis.viewmodel.middleContent.MapTrackingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val TAG = "LocUI"

@Composable
fun GoogleMapTrackingRoute(
    viewModel: MapTrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        Log.i(TAG, "GoogleMapTrackingRoute composed. Starting tracking.")
        viewModel.startTracking()
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.i(TAG, "GoogleMapTrackingRoute disposed. Stopping tracking.")
            viewModel.stopTracking()
        }
    }

    LaunchedEffect(uiState.cameraMoveSequence) {
        val target = uiState.cameraTarget ?: return@LaunchedEffect
        Log.d(
            TAG,
            "Animating camera to lat=${target.latitude}, lng=${target.longitude}, seq=${uiState.cameraMoveSequence}"
        )
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                LatLng(target.latitude, target.longitude),
                18f
            ),
            durationMs = 900
        )
    }

    GoogleMapTrackingScreen(
        cameraPositionState = cameraPositionState,
        latitude = uiState.currentLocation?.latitude,
        longitude = uiState.currentLocation?.longitude,
        onCenterClick = viewModel::centerOnUser
    )
}

@Composable
private fun GoogleMapTrackingScreen(
    cameraPositionState: CameraPositionState,
    latitude: Double?,
    longitude: Double?,
    onCenterClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCenterClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "Center on current location"
                )
            }
        }
    ) { paddingValues ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            if (latitude != null && longitude != null) {
                Marker(
                    state = MarkerState(position = LatLng(latitude, longitude)),
                    title = "Current Location"
                )
            }
        }
    }
}