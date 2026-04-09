package com.example.thesis.view.middleContent

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.thesis.view.middleContent.parts.middleContent.Damages

@Composable
fun MiddleContent(navController: NavController) {
    Damages(navController)
}