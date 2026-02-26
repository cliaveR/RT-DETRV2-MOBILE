package com.example.thesis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.view.bottomNavigationBar.BottomNavigationBar
import com.example.thesis.view.middleContent.parts.SelectedProjectDetails
import com.example.thesis.view.topBarContent.parts.ProjectTopBarCard
import com.example.thesis.view.topContent.ProjectTopContent

@Composable
fun ProjectPage(){
    Scaffold (
        topBar = {
            // set parameters
            ProjectTopBarCard(
                projectName = TODO(),
                onBackClick = TODO(),
                onNotificationClick = TODO(),
                modifier = TODO()
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }){
            innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
        {
            ProjectTopContent()
            SelectedProjectDetails()
        }
    }
}