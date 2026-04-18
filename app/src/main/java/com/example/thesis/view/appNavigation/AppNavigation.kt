package com.example.thesis.view.appNavigation

import android.Manifest
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.view.PermissionContent.PermissionGateWay
import com.example.thesis.view.appPages.MainPage
import com.example.thesis.view.appPages.MapPage
import com.example.thesis.view.cameraContent.CameraScreen
import com.example.thesis.view.cameraContent.videoContent.PhotoScreen
import com.example.thesis.view.cameraContent.videoContent.VideoScreen
import com.example.thesis.view.popUpContent.PopUpContent
import com.example.thesis.view.startups.launchScreen.LaunchScreenView
import com.example.thesis.view.topContent.parts.topContent.PictureCameraButtons
import com.example.thesis.view.uploadImageContent.UploadImagePage
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(navController: NavHostController) {

    val currentBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack.value?.destination?.route
        ?: NAVIGATIONPATH.SPLASH.route

    NavHost(
        navController = navController,
        startDestination = NAVIGATIONPATH.SPLASH.route
    ) {

        composable(NAVIGATIONPATH.SPLASH.route) {

            PermissionGateWay(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                rationale = "Location access is required to detect your current location."
            ) {

                LaunchScreenView()

                LaunchedEffect(Unit) {
                    delay(3000)
                    navController.navigate(NAVIGATIONPATH.MAIN.route) {
                        popUpTo(NAVIGATIONPATH.SPLASH.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        composable(NAVIGATIONPATH.MAIN.route) {
            MainNavigationContainer(
                currentRoute,
                navController
            ) {
                MainPage(navController)
            }
        }

        composable(NAVIGATIONPATH.MAP.route) {
            MainNavigationContainer(
                currentRoute,
                navController
            ) {
                MapPage(navController)
            }
        }

        composable(NAVIGATIONPATH.RESULTS.route) {
            MainNavigationContainer(
                currentRoute,
                navController
            ) {
                MainPage(navController)
            }
        }

        composable(NAVIGATIONPATH.UPLOAD.route) {
            MainNavigationContainer(
                currentRoute,
                navController
            ) {
                UploadImagePage(navController)
            }
        }

        composable("${NAVIGATIONPATH.DAMAGE.route}/{picture_id}") { backstackEntry ->
            val imageUri = backstackEntry.arguments
                ?.getString("picture_id")
                ?.let { Uri.parse(it) }

            MainNavigationContainer(
                currentRoute,
                navController
            ) {
                PopUpContent(imageUri, navController)
            }
        }

        composable(NAVIGATIONPATH.CAMERA.route) {
            CameraScreen(navController)
        }

        composable(NAVIGATIONPATH.PICTURE_VIDEO.route) {
            PictureCameraButtons(navController)
        }

        composable(NAVIGATIONPATH.VIDEO.route) {
            PermissionGateWay(
                permissions = listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                rationale = "Camera and Audio are required for video recording."
            ) {
                VideoScreen(navController)
            }
        }

        composable(NAVIGATIONPATH.PICTURE.route) {
            PermissionGateWay(
                permissions = listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                rationale = "Camera and Audio are required for taking photos."
            ) {
                PhotoScreen(navController = navController)
            }
        }
    }
}