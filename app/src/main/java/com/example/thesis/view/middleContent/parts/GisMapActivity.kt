package com.example.thesis.view.middleContent.parts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment

class GisMap : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ArcGISEnvironment.apiKey = ApiKey.create("AAPTxy8BH1VEsoebNVZXo8HurJPXzMSQBTGd-8NWrv87roChenBvctCxKS5BbOykPkM_mTN6I4CgDctyp6f_4gCeWlXpM0BDXLRXEj3lTNdFGVJHo18jzGCKa5M9ciZLhRyTk0ETgPFE12P2FXsnQ4oiD-DZKF09Rtgbb1-GP3PZyie53EmR5-KuHH7k37Jw7kYUDMWEAg7BJytAMIGBSK27G6H3NndSagT2qnIvWoa-TF8.AT1_UU9U6P0U")

        enableEdgeToEdge()

        setContent {
            GisMapTrial()
        }
    }
}