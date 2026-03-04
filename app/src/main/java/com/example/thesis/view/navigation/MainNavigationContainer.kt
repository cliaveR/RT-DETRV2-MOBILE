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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesis.R
import com.example.thesis.view.bottomNavigationBar.parts.BottomNavigationBar
import com.example.thesis.view.topBarContent.parts.NewPageTopBarCard
import com.example.thesis.view.topBarContent.parts.ProjectTopBarCard
import com.example.thesis.view.topBarContent.parts.TopBarCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationContainer(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
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
                    when (selectedTab) {
                        0, 1 -> {
                            TopBarCard(
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onNotificationClick = { /* TODO */ }
                            )
                        }
                        4 -> {
                            ProjectTopBarCard(
                                projectName = null,
                                onBackClick = { onTabSelected(0) },
                                onNotificationClick = { /* TODO */ }
                            )
                        }
                        else -> {
                            NewPageTopBarCard(
                                onMenuClick = { onTabSelected(0) },
                                onNotificationClick = { /* TODO */ }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (selectedTab <= 1) {
                    BottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected
                    )
                }
            }
        ) { innerPadding ->
            content(innerPadding)
        }
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
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        NavigationItem(label = "About us") { /* Navigate */ }
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
        Text(text = label, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}