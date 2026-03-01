package com.example.thesis.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.thesis.view.middleContent.MapContent
import com.example.thesis.view.slideUp.SlideUpContent
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage() {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 190.dp,
        sheetContainerColor = Color.White,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = {

            SlideUpContent()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MapContent()
        }
    }
}