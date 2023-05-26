package com.laul.trackaid.activities


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.laul.trackaid.Header
import com.laul.trackaid.compCommon
import com.laul.trackaid.connection.GFitConnectManager
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.theme.TrackAidTheme
import java.io.DataOutput

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
6
        val healthConnectManager by lazy {
            GFitConnectManager(this as Activity)
        }

        DataProvider.gFitUpdate(this as Activity, healthConnectManager.permission )

        setContent {
            TrackAidTheme{
                compCommon(gFitConnectManager = healthConnectManager)
            }
        }
    }
}
