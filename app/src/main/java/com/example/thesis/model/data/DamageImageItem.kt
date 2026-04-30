package com.example.thesis.model.data

import android.net.Uri

data class DamageImageItem(
    val uri: Uri,
    val displayName: String,
    val dateAddedSeconds: Long,
    val inferenceData: String? = null,
    val processingTimeMs: Int? = null
)