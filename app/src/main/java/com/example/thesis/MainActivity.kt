package com.example.thesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.ui.theme.ThesisTheme
import com.example.thesis.view.MainPage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThesisTheme {

                MainPage()

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