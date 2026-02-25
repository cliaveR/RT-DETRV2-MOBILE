package com.example.thesis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.thesis.view.bottomNavigationBar.parts.BottomNavigationBar
import com.example.thesis.view.middleContent.MiddleContent
import com.example.thesis.view.topBarContent.TopBarContent
import com.example.thesis.view.topContent.TopContent

@Composable
fun MainPage(){
    Scaffold (
        topBar = {
            TopBarContent()
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

                TopContent()
                MiddleContent()
            }
        }
}