package com.example.thesis.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.view.bottomNavigationBar.parts.BottomNavigationBar
import com.example.thesis.view.middleContent.parts.BulkUploadCard
import com.example.thesis.view.topBarContent.parts.NewPageTopBarCard

@Preview
@Composable
fun UploadsPage(){
    Scaffold (

        topBar = {
            Box(
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
            ) {
                NewPageTopBarCard()
            }
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
            BulkUploadCard()
        }
    }
}