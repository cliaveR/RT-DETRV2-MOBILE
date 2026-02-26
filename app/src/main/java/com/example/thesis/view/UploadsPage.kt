package com.example.thesis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.view.bottomNavigationBar.parts.BottomNavigationBar
import com.example.thesis.view.middleContent.parts.BulkUploadCard
import com.example.thesis.view.topBarContent.parts.NewPageTopBarCard

@Preview
@Composable
fun NewPagePage(){
    Scaffold (
        topBar = {
            NewPageTopBarCard()
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