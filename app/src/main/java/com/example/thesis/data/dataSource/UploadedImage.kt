package com.example.thesis.data.dataSource

import java.time.LocalDateTime

data class UploadedImage(
    val id: String,
    val name: String,
    val uploadDate: LocalDateTime
)