package com.laul.trackaid


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.laul.trackaid.connection.BaseApplication
import com.laul.trackaid.connection.GFitConnectManager
import com.laul.trackaid.ui.theme.TrackAidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthConnectManager by lazy {
            GFitConnectManager(this as Activity)
        }

        setContent {
            TrackAidTheme{
                compMainModule(gFitConnectManager = healthConnectManager)
            }
        }
    }
}
