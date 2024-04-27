package com.laul.trackaid.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.health.connect.client.HealthConnectClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.laul.trackaid.compCommon
import com.laul.trackaid.theme.TrackAidTheme

class MainActivity : ComponentActivity() {
    // Firebase instance variables
    private var mFirebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth and check if the user is signed in
        if (FirebaseAuth.getInstance().currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }


        if (HealthConnectClient.sdkStatus(this) == HealthConnectClient.SDK_AVAILABLE) {
            // Health Connect is available.
            setContent {
                TrackAidTheme{
                    compCommon(this)
                }
            }
        } else
            Toast.makeText(
                this, "Health Connect is not available", Toast.LENGTH_SHORT
            ).show()
    }


}
