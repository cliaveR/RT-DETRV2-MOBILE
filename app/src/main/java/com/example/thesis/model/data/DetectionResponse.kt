package com.example.thesis.model.data

data class DetectionResponse(
    val id: Long,
    val frameId: String,
    val rawS3Key: String,
    val processedS3Key: String?,
    val inferenceMs: Int,
    val detectionsJson: String,
    val createdAt: String
)