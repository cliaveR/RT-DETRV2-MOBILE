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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.ui.theme.ThesisTheme
import com.example.thesis.view.ResultsPage
import com.example.thesis.view.launchScreen.LaunchScreenView
import kotlinx.coroutines.delay
import com.example.thesis.R
import com.example.thesis.view.MainPage
import com.example.thesis.view.UploadsPage
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
            MainPage()

        }
    }
}

@Composable
fun MainAppNavigation() {
    var showSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        delay(3000) // 3000ms = 3 seconds
        showSplashScreen = false
    }

    if (showSplashScreen) {
        LaunchScreenView()
    } else {
        MainNavigationContainer { innerPadding ->
            Box(modifier = androidx.compose.ui.Modifier.padding(innerPadding)) {
                MainPage()
            }
        }
    }
}

