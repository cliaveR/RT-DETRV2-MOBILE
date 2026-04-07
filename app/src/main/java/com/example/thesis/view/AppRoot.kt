package com.example.thesis.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.thesis.view.appNavigation.AppNavigation


@Composable
@Preview
fun AppRoot() {
    val navController = rememberNavController()
    AppNavigation(navController)
}