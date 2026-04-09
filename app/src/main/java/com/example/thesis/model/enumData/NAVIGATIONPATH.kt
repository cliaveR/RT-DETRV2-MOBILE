package com.example.thesis.model.enumData

enum class NAVIGATIONPATH {
CAMERA,UPLOAD,SPLASH,MAIN,MAP,RESULTS,DAMAGE,PICTURE_VIDEO,VIDEO,PICTURE;
    val route: String get() = name.lowercase()
}