package com.example.thesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.ui.theme.ThesisTheme
import com.example.thesis.view.launchScreen.LaunchScreenView
import kotlinx.coroutines.delay
import com.example.thesis.view.MainPage
import com.example.thesis.view.ProjectPage
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
        startDestination = NAVIGATIONPATH.SPLASH.route
    ){
        composable (NAVIGATIONPATH.SPLASH.route){
            LaunchScreenView()
            LaunchedEffect(Unit) {
                delay(300)
                navController.navigate(NAVIGATIONPATH.MAIN.route){
                    popUpTo(NAVIGATIONPATH.MAIN.route){inclusive = true}
                }
            }
        }

        composable (NAVIGATIONPATH.MAIN.route){
            MainNavigationContainer(
                currentRoute = NAVIGATIONPATH.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NAVIGATIONPATH.MAP.route){
            MainNavigationContainer(
                currentRoute = NAVIGATIONPATH.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NAVIGATIONPATH.RESULTS.route){
            MainNavigationContainer(
                currentRoute = NAVIGATIONPATH.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }
        composable (NAVIGATIONPATH.UPLOAD.route){
            MainNavigationContainer(
                currentRoute = NAVIGATIONPATH.MAIN.route,
                navController = navController
            ) {
                innerPadding ->
                MainPage(
                    navController = navController
                )
            }
        }

        composable (NAVIGATIONPATH.PROJECT.route){
            MainNavigationContainer(
                currentRoute = NAVIGATIONPATH.MAIN.route,
                navController = navController
            ) {
                    innerPadding ->
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