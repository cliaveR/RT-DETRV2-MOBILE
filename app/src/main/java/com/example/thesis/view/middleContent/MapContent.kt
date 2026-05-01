package com.example.thesis.view.middleContent

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.thesis.view.middleContent.parts.mapContent.MapViewScreen

@Composable
fun MapContent(navController: NavController) {
    MapViewScreen(navController = navController)
}