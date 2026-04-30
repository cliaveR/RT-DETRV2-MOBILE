package com.example.thesis.view.appNavigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.thesis.view.popUpContent.PopUpVideoDamage
import com.example.thesis.view.startups.launchScreen.LaunchScreenView
import com.example.thesis.view.topContent.parts.topContent.PictureCameraButtons
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
                    launchSingleTop = true
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

        composable ("${NAVIGATIONPATH.DAMAGE.route}/{picture_id}"){
            backstackEntry ->
            val imageUri = backstackEntry.arguments
                ?.getString("picture_id")
                ?.let { Uri.parse(it) }
            MainNavigationContainer(
                currentRoute,navController
            ) {
                PopUpContent(imageUri,navController)
            }
        }

        composable ("${NAVIGATIONPATH.VIDEO_DAMAGE.route}/{video_uri}"){
            backstackEntry ->
            val videoUri = backstackEntry.arguments
                ?.getString("video_uri")
                ?.let { Uri.parse(it) }
            MainNavigationContainer(
                currentRoute,navController
            ) {
                PopUpVideoDamage(videoUri, navController)
            }
        }

        composable (NAVIGATIONPATH.CAMERA.route){
            CameraScreen(navController)
        }

        composable (NAVIGATIONPATH.PICTURE_VIDEO.route){
            PictureCameraButtons(navController)
        }
        composable (NAVIGATIONPATH.VIDEO.route) {
            PermissionGateWay(
                permissions = listOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                rationale = "Camera and Audio are required for your thesis video recording."
            ) {
                VideoScreen(navController=navController)
            }
        }
        composable (NAVIGATIONPATH.PICTURE.route) {
            PermissionGateWay(
                permissions = listOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,

                ),
                rationale = "Camera and Audio are required for your thesis video recording."
            ) {
                PhotoScreen(navController=navController)
            }
        }
    }
}
