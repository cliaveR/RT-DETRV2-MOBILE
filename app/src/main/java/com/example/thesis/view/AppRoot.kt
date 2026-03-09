package com.example.thesis.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.thesis.view.appNavigation.AppNavigation


@Composable
fun AppRoot() {
    val navController = rememberNavController()
    AppNavigation(navController)
}