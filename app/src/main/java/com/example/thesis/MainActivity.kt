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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.ui.theme.ThesisTheme
import com.example.thesis.view.AppRoot
import com.example.thesis.view.launchScreen.LaunchScreenView
import kotlinx.coroutines.delay
import com.example.thesis.view.appPages.MainPage
import com.example.thesis.view.cameraContent.CameraScreen
import com.example.thesis.view.appNavigation.MainNavigationContainer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThesisTheme {
                AppRoot()
            }
        }
    }
}


