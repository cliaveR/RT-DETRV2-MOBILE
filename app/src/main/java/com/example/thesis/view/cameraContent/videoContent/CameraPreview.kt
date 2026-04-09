package com.example.thesis.view.cameraContent.videoContent

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(modifier: Modifier = Modifier, onUseCaseCreated: (Preview)-> Unit){
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    AndroidView(factory = {previewView}, modifier= Modifier){
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        onUseCaseCreated(preview)
    }
}