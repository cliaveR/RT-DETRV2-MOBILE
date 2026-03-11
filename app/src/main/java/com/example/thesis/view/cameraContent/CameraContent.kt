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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CameraScreen(navController: NavController) {
    var permission by remember { mutableStateOf(false) }

    val getPermissionUser = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permission = isGranted
    }

    LaunchedEffect(Unit) {
        getPermissionUser.launch(android.Manifest.permission.CAMERA)
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
    // ... existing code ...
    var currentLocation by remember { mutableStateOf<android.location.Location?>(null) }   // ✅ NEW

    // ✅ NEW: Fetch last known location
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    LaunchedEffect(Unit) {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc -> currentLocation = loc }
    }

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
                    location = currentLocation,    // ✅ pass location
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
    var permission by remember { mutableStateOf(false) }
    val getPermissionUser = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()  // ✅ multiple
    ) { permissions ->
        permission = permissions[android.Manifest.permission.CAMERA] == true  // ✅ check camera specifically
    }
    LaunchedEffect(Unit) {
        getPermissionUser.launch(                                         // ✅ launch array
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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
fun saveLocationToExif(
    context: Context,
    uri: android.net.Uri,
    latitude: Double,
    longitude: Double
) {
    try {
        context.contentResolver.openFileDescriptor(uri, "rw")?.use { pfd ->
            val exif = androidx.exifinterface.media.ExifInterface(pfd.fileDescriptor)
            exif.setGpsInfo(
                android.location.Location("").also {
                    it.latitude = latitude
                    it.longitude = longitude
                }
            )
            exif.saveAttributes()
            Log.d("EXIF", "Location saved: lat=$latitude, lon=$longitude")
        }
    } catch (e: Exception) {
        Log.e("EXIF", "Failed to save location to EXIF: ${e.message}")
    }
}
fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    scope: CoroutineScope,
    location: android.location.Location?,
    onUploadSuccess: (String, android.graphics.Bitmap?) -> Unit // ✅ bitmap as param
) {
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
                    val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                        android.graphics.ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }
                    val filename = uri.lastPathSegment ?: "image.jpg"

                    // ✅ Save location to image EXIF metadata
                    location?.let {
                        saveLocationToExif(context, uri, it.latitude, it.longitude)
                    }

                    withContext(Dispatchers.Main) {
                        onUploadSuccess(filename, bitmap)
                    }
                }
            }
        }
    )
}