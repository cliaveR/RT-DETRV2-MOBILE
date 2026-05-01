package com.example.thesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.thesis.ui.theme.ThesisTheme
import com.example.thesis.view.AppRoot


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThesisTheme {
                //astig
                //ok
                AppRoot()
            }
        }
    }
}


