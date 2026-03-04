package com.example.thesis.data.dataSource

import java.time.LocalDateTime

data class Project(
    val id: String,
    val name: String,
    val lastOpened: LocalDateTime
)