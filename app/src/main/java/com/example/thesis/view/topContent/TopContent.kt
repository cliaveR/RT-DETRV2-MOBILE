package com.example.thesis.view.topContent

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.thesis.view.topContent.parts.CameraAndUploadCard
import com.example.thesis.view.topContent.parts.DetectionCard

@Composable
fun TopContent (navController: NavController){
    CameraAndUploadCard(navController)
}