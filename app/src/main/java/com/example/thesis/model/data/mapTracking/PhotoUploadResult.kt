package com.example.thesis.model.data.mapTracking

import android.net.Uri

data class PhotoUploadResult(
    val frameId: String?,
    val processingTimeMs: Int?,
    val inferenceData: String?,
    val savedImageUri: Uri?
)