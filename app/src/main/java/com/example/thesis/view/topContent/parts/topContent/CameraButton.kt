package com.example.thesis.view.topContent.parts.topContent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thesis.model.enumData.NAVIGATIONPATH

@Composable
fun CameraButton(navController: NavController) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray, RoundedCornerShape(12.dp))
            .clickable {
                navController.navigate(NAVIGATIONPATH.PICTURE_VIDEO.route)
            }, // just like ProjectCard
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoCamera,
            contentDescription = "Camera",
            tint = Color.Black,
            modifier = Modifier.size(32.dp)
        )
    }
}