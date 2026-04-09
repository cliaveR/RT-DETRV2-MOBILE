package com.example.thesis.view.appPages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.thesis.view.middleContent.MiddleContent
import com.example.thesis.view.topContent.TopContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun MainPage(navController: NavController) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            TopContent(navController = navController)
            MiddleContent(navController)
        }

}

@Preview
@Composable
fun MainPagePreview() {
    MainPage(navController = rememberNavController())
}