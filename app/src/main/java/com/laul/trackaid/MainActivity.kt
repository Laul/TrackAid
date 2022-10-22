package com.laul.trackaid


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.laul.trackaid.ui.theme.TrackAidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val healthConnectManager = (application as BaseApplication).healthConnectManager

        setContent {
            TrackAidTheme{
                compMainModule(gFitConnectManager = healthConnectManager)
            }
        }
    }
}
