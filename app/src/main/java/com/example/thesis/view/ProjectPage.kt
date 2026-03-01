package com.example.thesis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.view.middleContent.ProjectMiddleContent
import com.example.thesis.view.topContent.ProjectGISTopContent

@Preview
@Composable
fun ProjectPage(){
    Scaffold (
      ){
            innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
        {
            ProjectGISTopContent()
            ProjectMiddleContent()
        }
    }
}