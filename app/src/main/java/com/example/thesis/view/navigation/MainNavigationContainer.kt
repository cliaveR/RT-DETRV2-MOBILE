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
import androidx.navigation.NavController
import com.example.thesis.R
import com.example.thesis.data.dataSource.NavigationPath
import com.example.thesis.view.bottomNavigationBar.parts.BottomNavigationBar
import com.example.thesis.view.topBarContent.parts.HomeTopBar
import com.example.thesis.view.topBarContent.parts.ProjectTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationContainer(
    currentRoute:String,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val showBottomBar = currentRoute in listOf(NavigationPath.MAIN.route, NavigationPath.MAP.route)

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
                    val onNotify = { /* Your notification logic */ }

                    when (currentRoute) {
                        // 0 -> MainPage (Hamburger Menu + Logo)
                        NavigationPath.MAIN.route -> HomeTopBar(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onNotify = onNotify
                        )

                          // 1 -> MapPage (Back Button, No Title)

                        else -> HomeTopBar(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onNotify = onNotify
                        )
                    }
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(
                        selectedTab = 0,
                        onTabSelected = {
                            tab -> when(tab){
                            0 -> navController.navigate(NavigationPath.MAIN.route)
                            1 -> navController.navigate(NavigationPath.MAP.route)
                            }
                        }
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