package com.example.thesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesis.ui.theme.ThesisTheme
<<<<<<< HEAD
import com.example.thesis.view.MainScaffolding
=======
>>>>>>> e808ce4c7f0bd9d10dcf8637ad7e2058df1edaf2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThesisTheme {
<<<<<<< HEAD
            MainScaffolding()
=======
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
>>>>>>> e808ce4c7f0bd9d10dcf8637ad7e2058df1edaf2
            }
        }
    }
}

<<<<<<< HEAD

=======
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
>>>>>>> e808ce4c7f0bd9d10dcf8637ad7e2058df1edaf2

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThesisTheme {
<<<<<<< HEAD
        MainScaffolding()
=======
        Greeting("Android")
>>>>>>> e808ce4c7f0bd9d10dcf8637ad7e2058df1edaf2
    }
}