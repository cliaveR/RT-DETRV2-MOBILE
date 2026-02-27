package com.example.thesis.model.middleContent

import java.time.LocalDateTime

data class UploadedImage(
    val id: String,
    val name: String,
    val uploadDate: LocalDateTime
)