package com.example.thesis.view.bottomNavigationBar.parts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
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

    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,         // removes gray tone
    ) {

        // Home button (index 0)
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") }
        )

        // Map button (index 1)
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = "Map"
                )
            },
            label = { Text("Map") }
        )
    }
}