package com.example.thesis

import android.app.Application
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment

class ThesisApplication : Application() {
    override fun onCreate() {
        super.onCreate()

    ArcGISEnvironment.apiKey = ApiKey.create("AAPTxy8BH1VEsoebNVZXo8HurJPXzMSQBTGd-8NWrv87roChenBvctCxKS5BbOykPkM_mTN6I4CgDctyp6f_4gCeWlXpM0BDXLRXEj3lTNdFGVJHo18jzGCKa5M9ciZLhRyTk0ETgPFE12P2FXsnQ4oiD-DZKF09Rtgbb1-GP3PZyie53EmR5-KuHH7k37Jw7kYUDMWEAg7BJytAMIGBSK27G6H3NndSagT2qnIvWoa-TF8.AT1_UU9U6P0U")
    }
}