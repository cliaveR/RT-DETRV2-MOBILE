@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.thesis.view.appPages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.thesis.view.PermissionContent.PermissionGateWay
import com.example.thesis.view.middleContent.MapContent

@Composable
fun MapPage(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PermissionGateWay(
            permissions = listOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            rationale = "Location access is required to track and center your real-time position on the map."
        ) {
            MapContent(navController = navController)
        }
    }
}
