package com.example.thesis.model.data

import android.net.Uri

data class GalleryImage(
    val uri: Uri,
    val isSelected: Boolean = false
)
