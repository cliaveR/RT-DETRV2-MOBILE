package com.example.thesis.view.bottomNavigationBar.parts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun BottomNavigationBar() {

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,         // removes gray tone
    ) {

        // Home button (index 0)
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color.Black
                )
            },
            label = { Text("Home") }
        )

        // Map button (index 1)
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = "Map",
                    tint = Color.Black
                )
            },
            label = { Text("Map", color = Color.Black) }
        )
    }
}