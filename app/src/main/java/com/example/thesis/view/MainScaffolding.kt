package com.example.thesis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.thesis.view.middleContent.MiddleContent
import com.example.thesis.view.topContent.TopContent

@Composable
fun MainScaffolding() {

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Text(text = "show more")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            TopContent()

            MiddleContent()

        }
    }
}