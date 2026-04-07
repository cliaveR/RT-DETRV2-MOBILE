package com.example.thesis.view.appNavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.view.appPages.MainPage
import com.example.thesis.view.appPages.MapPage
import com.example.thesis.view.cameraContent.CameraScreen
import com.example.thesis.view.launchScreen.LaunchScreenView
import com.example.thesis.view.uploadImageContent.UploadImagePage
import kotlinx.coroutines.delay


@Composable
fun AppNavigation(navController: NavHostController) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: NAVIGATIONPATH.SPLASH.route

    NavHost(
        navController = navController,
        startDestination = NAVIGATIONPATH.SPLASH.route
    ){
        composable (NAVIGATIONPATH.SPLASH.route){
            LaunchScreenView()
            LaunchedEffect(Unit) {
                delay(3000)
                navController.navigate(NAVIGATIONPATH.MAIN.route){
                    popUpTo(NAVIGATIONPATH.MAIN.route){inclusive = true}
                }
            }
        }

        composable (NAVIGATIONPATH.MAIN.route){
            MainNavigationContainer(
                currentRoute,navController
            ) {
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NAVIGATIONPATH.MAP.route){
            MainNavigationContainer(
                currentRoute,navController
            ) {
                MapPage(
                    navController = navController
                )
            }
        }
        composable (NAVIGATIONPATH.RESULTS.route){
            MainNavigationContainer(
                currentRoute,navController
            ) {
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NAVIGATIONPATH.UPLOAD.route){
            MainNavigationContainer(
                currentRoute,navController
            ) {
                UploadImagePage(navController)
            }
        }

        composable (NAVIGATIONPATH.PROJECT.route){
            MainNavigationContainer(
                currentRoute,navController
            ) {
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NAVIGATIONPATH.CAMERA.route){
            CameraScreen(navController)
        }
    }

}

