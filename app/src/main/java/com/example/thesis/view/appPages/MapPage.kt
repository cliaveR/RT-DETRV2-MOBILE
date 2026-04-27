@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.thesis.view.appPages

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thesis.view.PermissionContent.PermissionGateWay
import com.example.thesis.view.middleContent.MapContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage(navController: NavHostController) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 190.dp,
        sheetContainerColor = Color.White,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = {}
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PermissionGateWay(
                permissions = listOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                rationale = "Location access is required to track and center your real-time position on the map."
            ) {
                MapContent()
            }
        }
    }
}

@Composable
@Preview
fun showMapPage(navHostController: NavHostController) {
    MapPage(navHostController)
}