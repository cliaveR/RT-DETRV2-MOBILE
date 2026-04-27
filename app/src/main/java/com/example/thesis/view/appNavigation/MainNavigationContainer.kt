package com.example.thesis.view.appNavigation

import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.thesis.R
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.view.appNavigation.sideBarContent.SidebarContent
import com.example.thesis.view.bottomNavigationBar.BottomNavBar
import com.example.thesis.view.bottomNavigationBar.parts.BottomNavigationBar
import com.example.thesis.view.topBarContent.HomeTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationContainer(
    currentRoute:String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //this shows if we are mainly on the main and map pages we don't want to render unless is this route
    val showBottomBar = currentRoute in listOf(NAVIGATIONPATH.MAIN.route, NAVIGATIONPATH.MAP.route)

    var selectedTab = when(currentRoute){
        NAVIGATIONPATH.MAIN.route -> 0
        NAVIGATIONPATH.MAP.route -> 1
        else -> 0
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp),
                drawerContainerColor = Color.White,
                drawerShape = RectangleShape
            ) {
                SidebarContent(
                    onGoMain = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NAVIGATIONPATH.MAIN.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onGoMap = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NAVIGATIONPATH.MAP.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onClose = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = { TopBarContent(currentRoute,scope,drawerState) },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            val destination = when (tab) {
                                0 -> NAVIGATIONPATH.MAIN.route
                                1 -> NAVIGATIONPATH.MAP.route
                                else -> NAVIGATIONPATH.MAIN.route
                            }
                            navController.navigate(destination) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()  // padding handled here, not pushed down
            }
        }
    }
}

@Composable
fun TopBarContent(currentRoute: String,scope: CoroutineScope,drawerState: DrawerState){
    Box(
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color.White)

    ) {
        val onNotify = { /* Your notification logic */ }

        when (currentRoute) {
            NAVIGATIONPATH.MAIN.route -> HomeTopBar(
                onMenuClick = { scope.launch { drawerState.open() } },
                onNotify = onNotify
            )

            else -> HomeTopBar(
                onMenuClick = { scope.launch { drawerState.open() } },
                onNotify = onNotify
            )
        }
    }
}
