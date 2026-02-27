package com.example.thesis.view
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.view.bottomNavigationBar.parts.BottomNavigationBar
import com.example.thesis.view.middleContent.DetectionDetailsMiddleContent
import com.example.thesis.view.topBarContent.parts.NewPageTopBarCard
import com.example.thesis.view.topContent.DetectionTopContent

@Preview
@Composable
fun ResultsPage(){
    val scrollState = rememberScrollState()
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
                .verticalScroll(scrollState)
        )
        {
            DetectionTopContent()
            DetectionDetailsMiddleContent()
        }
    }
}