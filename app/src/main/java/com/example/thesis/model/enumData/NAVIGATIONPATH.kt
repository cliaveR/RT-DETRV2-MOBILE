package com.example.thesis.model.enumData

enum class NAVIGATIONPATH {
CAMERA,UPLOAD,SPLASH,MAIN,MAP,RESULTS,PROJECT;
    val route: String get() = name.lowercase()
}