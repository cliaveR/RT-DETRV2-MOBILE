package com.example.thesis.model.data

import java.time.LocalDateTime

data class Upload(
    val id: String,
    val fileName: String,
    val uploadDate: LocalDateTime
)