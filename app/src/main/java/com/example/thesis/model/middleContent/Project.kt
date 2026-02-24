package com.example.thesis.model.middleContent

import java.time.LocalDateTime

data class Project(
    val id: String,
    val name: String,
    val lastOpened: LocalDateTime
)