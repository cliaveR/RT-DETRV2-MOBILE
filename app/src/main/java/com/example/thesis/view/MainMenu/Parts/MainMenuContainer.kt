package com.example.thesis.view.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesis.R
import com.example.thesis.view.topBarContent.parts.MenuCard
import kotlinx.coroutines.launch

@Composable
fun MainNavigationContainer(
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp),
                drawerContainerColor = Color.White,
                drawerShape = RectangleShape
            ) {
                SidebarContent()
            }
        }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(WindowInsets.statusBars.asPaddingValues())
                ) {
                    MenuCard(
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        onNotificationClick = {
                        }
                    )
                }
            },
            content = { innerPadding ->
                content(innerPadding)
            }
        )
    }
}

@Composable
fun SidebarContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_logo),
            contentDescription = "G8KIPPERS Logo",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 2. Navigation Items
        NavigationItem(label = "About us") {
            // Handle Navigation to About Us
        }

        // Add more items here if needed
        // NavigationItem(label = "Settings") { }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Copyright © 2026",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun NavigationItem(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}