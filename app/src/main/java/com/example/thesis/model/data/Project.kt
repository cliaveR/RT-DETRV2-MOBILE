package com.example.thesis.model.data

import java.time.LocalDateTime

data class Project(
    val id: String,
    val name: String,
    val lastOpened: LocalDateTime
)