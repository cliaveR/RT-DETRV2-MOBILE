package com.example.thesis.model.data

import android.net.Uri

data class DamageVideoItem(
    val uri: Uri,
    val displayName: String,
    val dateAddedSeconds: Long?,
    val processingTimeMs: Int? = null,
    val startLatitude: Double? = null,
    val startLongitude: Double? = null,
    val endLatitude: Double? = null,
    val endLongitude: Double? = null
)
