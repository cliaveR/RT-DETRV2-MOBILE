package com.example.thesis.view.popUpContent

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.model.data.mapTracking.PhotoUploadResult
import com.example.thesis.view.popUpContent.parts.ImageDetailsCard
import com.example.thesis.view.popUpContent.parts.PictureResultCard

@Composable
fun PopUpContent(imageUri: Uri?, navController: NavController, result: PhotoUploadResult? = null) {
    val context = LocalContext.current
    val repository = remember { PhotoRepository(context) }
    var dbResult by remember { mutableStateOf<PhotoUploadResult?>(null) }

    LaunchedEffect(imageUri) {
        if (result == null && imageUri != null) {
            val item = repository.getImageDetails(imageUri)
            if (item != null) {
                dbResult = PhotoUploadResult(
                    frameId = null,
                    processingTimeMs = item.processingTimeMs,
                    inferenceData = item.inferenceData,
                    savedImageUri = imageUri,
                    latitude = item.latitude,
                    longitude = item.longitude
                )
            }
        }
    }

    val finalResult = result ?: dbResult

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
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

        PictureResultCard(imageUri)
        Spacer(modifier = Modifier.height(50.dp))
        ImageDetailsCard(result = finalResult, imageUri = imageUri)
    }
}