package com.example.thesis

import android.app.Application
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment

class ThesisApplication : Application() {
    override fun onCreate() {
        super.onCreate()

    ArcGISEnvironment.apiKey = ApiKey.create("AAPTaM41L9JAKJxnMdUuthpfo2Q..gHu1r4Y5MjA4k1Gc7YZM2duZlJCequSnWSYA8jcAHXV7zmBjeoiq8A2LDorJ6var5a-VLKvQ-XYSiB0s0wugJfo0eOG4ru9kq6quNHS0rJNFkVourDFrbX9JQB2x6xwoiJMW0fLc1LLWMhWranWR_S9o2bk-swS-zHIfZfKuiO_U9OpKAQ-h5EXbKUmKtXKtzooVQ-LyyqfEQplKDwUCXKmxy4cSGrlfLgfmszubth_Sk41iUdlDRKTmq5M.AT1_UU9U6P0U")
    }
}