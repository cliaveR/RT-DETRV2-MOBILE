package com.example.thesis.model.data

import java.time.LocalDateTime

data class UploadedImage(
    val id: String,
    val name: String,
    val uploadDate: LocalDateTime
)