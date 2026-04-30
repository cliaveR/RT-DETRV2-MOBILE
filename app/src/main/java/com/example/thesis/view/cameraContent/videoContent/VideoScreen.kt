package com.example.thesis.view.cameraContent.videoContent

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.thesis.domain.camera.CameraManager
import com.example.thesis.domain.repository.VideoRepository
import com.example.thesis.viewmodel.CameraContent.VideoViewModel
import com.example.thesis.viewmodel.CameraContent.VideoViewModelFactory
import com.example.thesis.domain.location.CurrentLocationProvider
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("MissingPermission")
@Composable
fun VideoScreen(navController: NavController) {
    val context = LocalContext.current

    val repository = remember {
        VideoRepository(context, "http://192.168.254.201:8080/api/upload/video")
    }

    val locationProvider = remember { CurrentLocationProvider(context) }

    val viewModel: VideoViewModel = viewModel(
        factory = VideoViewModelFactory(
            repository = repository,
            locationProvider = locationProvider
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager(context) }

    // Observe toast messages from ViewModel
    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(modifier = Modifier.fillMaxSize()) { previewUseCase ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val videoCapture = cameraManager.buildVideoCapture()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    previewUseCase,
                    videoCapture
                )
            }, ContextCompat.getMainExecutor(context))
        }

        if (viewModel.isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(color = Color.White)
                Text(
                    text = "AI is detecting road damage...",
                    color = Color.White,
                    modifier = Modifier.padding(top = 80.dp)
                )
            }
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
        ) {
            Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.White)
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Button(
                onClick = @androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO) {
                    viewModel.toggleRecording(cameraManager)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = !viewModel.isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.isRecording) Color.Red else Color.Blue
                )
            ) {
                Text(
                    text = when {
                        viewModel.isProcessing -> "Processing..."
                        viewModel.isRecording -> "Stop & Analyze"
                        else -> "Start Recording"
                    }
                )
            }
        }
    }
}
