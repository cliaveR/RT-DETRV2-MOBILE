package com.example.thesis.view.videoContent

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thesis.model.data.VideoMarker
import com.example.thesis.model.`object`.LocalVideoMarkerStore
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.coroutines.resume
import java.util.concurrent.TimeUnit

@Composable
fun VideoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var permission by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Ready to record video") }
    var startLocation by remember { mutableStateOf<android.location.Location?>(null) }
    var recordingStartTime by remember { mutableStateOf(0L) }
    var isUploading by remember { mutableStateOf(false) }

    val getPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        permission = perms[android.Manifest.permission.CAMERA] == true
    }

    LaunchedEffect(Unit) {
        getPermissions.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.RECORD_AUDIO
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1a1a1a))) {
        // Placeholder UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text("📹 Video Recording\n(Coming Soon)", color = Color.White)
        }

        // Status message
        Text(
            text = statusMessage,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            color = Color.White
        )

        if (isUploading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color = Color.White
            )
        }

        // Back button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Simulate Record button
        Button(
            onClick = {
                if (!isRecording && permission) {
                    scope.launch {
                        isRecording = true
                        statusMessage = "🔴 Recording..."
                        startLocation = fetchLastLocation(context)
                        recordingStartTime = System.currentTimeMillis()
                    }
                } else if (isRecording) {
                    // Simulate stop
                    isRecording = false
                    val endTime = System.currentTimeMillis()
                    val duration = (endTime - recordingStartTime) / 1000

                    statusMessage = "Processing & uploading..."
                    isUploading = true

                    scope.launch {
                        // Simulate upload
                        val endLocation = try {
                            fetchLastLocation(context)
                        } catch (e: Exception) {
                            null
                        }

                        if (startLocation != null && endLocation != null) {
                            val videoMarker = VideoMarker(
                                id = "video_${System.currentTimeMillis()}",
                                startLatitude = startLocation!!.latitude,
                                startLongitude = startLocation!!.longitude,
                                endLatitude = endLocation.latitude,
                                endLongitude = endLocation.longitude,
                                severity = 2,
                                damageType = "Video Survey",
                                videoUrl = null,
                                recordedAt = java.time.Instant.now().toString(),
                                durationSeconds = duration
                            )
                            LocalVideoMarkerStore.addVideoMarker(videoMarker)
                            statusMessage = "✅ Video recorded!"
                            isUploading = false
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text(if (isRecording) "⏹ Stop Recording" else "🎥 Start Recording")
        }
    }
}

// Try to obtain the last known location
suspend fun fetchLastLocation(context: Context): android.location.Location? {
    return suspendCancellableCoroutine { cont ->
        try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            @Suppress("MissingPermission")
            client.lastLocation
                .addOnSuccessListener { location -> cont.resume(location) }
                .addOnFailureListener { cont.resume(null) }
        } catch (e: Exception) {
            cont.resume(null)
        }
    }
}




