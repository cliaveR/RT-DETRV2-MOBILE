package com.example.thesis.view.topContent

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.thesis.view.topContent.parts.topContent.CameraAndUploadHolder

@Composable
fun TopContent (navController: NavController){
    CameraAndUploadHolder(navController)
}