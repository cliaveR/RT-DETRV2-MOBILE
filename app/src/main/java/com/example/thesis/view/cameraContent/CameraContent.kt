package com.example.thesis.view.cameraContent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.thesis.domain.repository.MapMarkerRepository
import com.example.thesis.model.data.MapMarker
import com.example.thesis.model.`object`.LocalMarkerStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun CameraScreen(navController: NavController) {
    var permission by remember { mutableStateOf(false) }

    val getPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        permission = perms[android.Manifest.permission.CAMERA] == true
    }

    LaunchedEffect(Unit) {
        getPermissions.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_FINE_LOCATION))
    }

    if (permission) {
        CameraPreview(navController)
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required")
        }
    }
}

@Composable
fun CameraPreview(navController: NavController) {
    val context = LocalContext.current
    val localLifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val previewView = remember { PreviewView(context) }
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }

    var downloadedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var statusMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Show captured image
        downloadedBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }

        // Show status message
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
        }

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
        // Capture button
        Button(
            onClick = {
                capturePhoto(
                    context = context,
                    imageCapture = imageCapture,
                    scope = scope,
                    onUploadSuccess = { filename, resultBitmap -> // ✅ receive bitmap
                        downloadedBitmap = resultBitmap
                        if (resultBitmap != null) {
                            saveImageToGallery(context, resultBitmap, filename)
                            statusMessage = "✅ Image received!"
                        } else {
                            statusMessage = "❌ Failed to receive image"
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Capture")
        }
    }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            preview.surfaceProvider = previewView.surfaceProvider
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                localLifecycle,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(context))
    }
}
fun saveImageToGallery(
    context: Context,
    bitmap: android.graphics.Bitmap,
    filename: String
) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "captured_$filename")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        Log.d("Gallery", "Image saved: $filename")
    }
}

// Save image to gallery and return the URI for storage in markers
fun saveImageToGalleryAndGetUri(
    context: Context,
    bitmap: android.graphics.Bitmap,
    filename: String
): android.net.Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "processed_$filename")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        Log.d("Gallery", "Processed image saved: $filename, URI: $uri")
    }
    return uri
}

// Try to obtain the last known location. This may return null if location permission
// hasn't been granted or no location is available yet.
@SuppressLint("MissingPermission")
suspend fun fetchLastLocation(context: Context): android.location.Location? {
    return suspendCoroutine { cont ->
        try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.lastLocation
                .addOnSuccessListener { location ->
                    cont.resume(location)
                }
                .addOnFailureListener { _ ->
                    cont.resume(null)
                }
        } catch (e: Exception) {
            cont.resume(null)
        }
    }
}

fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    scope: CoroutineScope,
    onUploadSuccess: (String, android.graphics.Bitmap?) -> Unit
) {
    val markerRepository = MapMarkerRepository()
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Photo did not save!", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Photo saved!", Toast.LENGTH_SHORT).show()
                val uri = outputFileResults.savedUri ?: return

                scope.launch(Dispatchers.IO) {
                                try {
                                    // Attempt to fetch last known location (may be null if permission not granted)
                                    val location = try {
                                        withContext(Dispatchers.Main) { fetchLastLocation(context) }
                                    } catch (e: Exception) {
                                        null
                                    }
                                    // ADD THIS:
                        Log.d("MarkerPopup", "fetchLastLocation result: lat=${location?.latitude} lon=${location?.longitude} — isNull=${location == null}")

                                    // Read image bytes from URI
                        val imageBytes = context.contentResolver
                            .openInputStream(uri)?.readBytes() ?: return@launch
                        val filename = "${System.currentTimeMillis()}.jpg"

                        // Send to Spring Boot /api/upload/visualize
                        val client = okhttp3.OkHttpClient.Builder()
                            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .build()

                        val requestBodyBuilder = okhttp3.MultipartBody.Builder()
                            .setType(okhttp3.MultipartBody.FORM)
                            .addFormDataPart(
                                "image",
                                filename,
                                imageBytes.toRequestBody(
                                    "image/jpeg".toMediaTypeOrNull(),
                                    0,
                                    imageBytes.size
                                )
                            )
                        // include location fields if available
                        if (location != null) {
                            requestBodyBuilder.addFormDataPart("latitude", location.latitude.toString())
                            requestBodyBuilder.addFormDataPart("longitude", location.longitude.toString())
                        }

                        val requestBody = requestBodyBuilder.build()

                        val request = okhttp3.Request.Builder()
                            .url("http://10.0.2.2:8080/api/upload/visualize")
                            .post(requestBody)
                            .build()

                        val response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            val responseBytes = response.body?.bytes()
                            val resultBitmap = responseBytes?.let {
                                android.graphics.BitmapFactory.decodeByteArray(it, 0, it.size)
                            }

                            // Save processed image to gallery and get its URI
                            var savedImageUri: String? = null
                            if (resultBitmap != null) {
                                savedImageUri = saveImageToGalleryAndGetUri(context, resultBitmap, filename)?.toString()
                            }

                            Log.d("MarkerPopup", "Upload success — location=$location savedUri=$savedImageUri")

                            withContext(Dispatchers.Main) {
                                onUploadSuccess(filename, resultBitmap)
                            }

                            // Add marker from upload response to local store with image URI
                            val markerLat = location?.latitude
                            val markerLon = location?.longitude
                            Log.d("MarkerPopup", "Adding marker? lat=$markerLat lon=$markerLon")

                            if (markerLat != null && markerLon != null) {
                                LocalMarkerStore.addMarker(
                                    MapMarker(
                                        id = filename,
                                        longitude = markerLon,
                                        latitude = markerLat,
                                        severity = 2,
                                        damageType = "Pothole",
                                        imageUrl = savedImageUri,
                                        capturedAt = java.time.Instant.now().toString()
                                    )
                                )
                                Log.d("MarkerPopup", "Marker added to LocalMarkerStore. Total=${LocalMarkerStore.getMarkers().size}")
                            } else {
                                Log.w("MarkerPopup", "Skipped adding marker — no location available")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                onUploadSuccess(filename, null)
                            }

                        }

                    } catch (e: Exception) {
                        Log.e("Upload", "Failed: ${e.message}")
                        withContext(Dispatchers.Main) {
                            onUploadSuccess("error", null)
                        }
                    }
                }
            }
        }
    )
}