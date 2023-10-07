package com.laul.trackaid.activities


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import com.laul.trackaid.compCommon
import com.laul.trackaid.theme.TrackAidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
6
        if (HealthConnectClient.sdkStatus(this) == HealthConnectClient.SDK_AVAILABLE) {
            // Health Connect is available.
            setContent {
                TrackAidTheme{
                    compCommon()
                }
            }
        } else
            Toast.makeText(
                this, "Health Connect is not available", Toast.LENGTH_SHORT
            ).show()
        }

}
