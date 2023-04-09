package com.laul.trackaid.activities


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.laul.trackaid.Header
import com.laul.trackaid.compCommon
import com.laul.trackaid.connection.GFitConnectManager
import com.laul.trackaid.theme.TrackAidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthConnectManager by lazy {
            GFitConnectManager(this as Activity)
        }

        setContent {
            TrackAidTheme{
                Header()
                compCommon(gFitConnectManager = healthConnectManager)
            }
        }
    }
}
