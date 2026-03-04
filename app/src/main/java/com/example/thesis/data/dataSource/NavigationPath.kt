package com.example.thesis.data.dataSource

enum class NavigationPath {
CAMERA,UPLOAD,SPLASH,MAIN,MAP,RESULTS,PROJECT;
    val route: String get() = name.lowercase()
}