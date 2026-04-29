package com.example.thesis.model.data

import android.net.Uri

data class DamageVideoItem(
    val uri: Uri,
    val displayName: String,
    val dateAddedSeconds: Long?
)
