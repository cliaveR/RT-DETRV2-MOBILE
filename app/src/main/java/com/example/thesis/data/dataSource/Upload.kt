package com.example.thesis.data.dataSource

import java.time.LocalDateTime

data class Upload(
    val id: String,
    val fileName: String,
    val uploadDate: LocalDateTime
)