package com.example.thesis.model.middleContent

import java.time.LocalDateTime

data class Upload(
    val id: String,
    val fileName: String,
    val uploadDate: LocalDateTime
)