package com.example.thesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thesis.data.dataSource.NavigationPath
import com.example.thesis.ui.theme.ThesisTheme
import com.example.thesis.view.launchScreen.LaunchScreenView
import kotlinx.coroutines.delay
import com.example.thesis.view.MainPage
import com.example.thesis.view.MapPage
import com.example.thesis.view.ProjectPage
import com.example.thesis.view.ResultsPage
import com.example.thesis.view.UploadsPage
import com.example.thesis.view.cameraContent.CameraScreen
import com.example.thesis.view.navigation.MainNavigationContainer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThesisTheme {
                MainAppNavigation()
            }
        }
    }




    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ThesisTheme {
            ProjectPage()

        }
    }
}

@Composable
fun MainAppNavigation() {
    var showSplashScreen by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationPath.SPLASH.route
    ){
        composable (NavigationPath.SPLASH.route){
            LaunchScreenView()
            LaunchedEffect(Unit) {
                delay(300)
                navController.navigate(NavigationPath.MAIN.route){
                    popUpTo(NavigationPath.MAIN.route){inclusive = true}
                }
            }
        }

        composable (NavigationPath.MAIN.route){
            MainNavigationContainer(
                currentRoute = NavigationPath.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NavigationPath.MAP.route){
            MainNavigationContainer(
                currentRoute = NavigationPath.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NavigationPath.RESULTS.route){
            MainNavigationContainer(
                currentRoute = NavigationPath.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NavigationPath.UPLOAD.route){
            MainNavigationContainer(
                currentRoute = NavigationPath.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }

        composable (NavigationPath.PROJECT.route){
            MainNavigationContainer(
                currentRoute = NavigationPath.MAIN.route,
                navController = navController
            ) {
                    innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NavigationPath.CAMERA.route){
            CameraScreen(navController)
        }

    }

}