package com.laul.trackaid.activities

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.HttpAuthenticationMethod
import com.google.android.fhir.sync.HttpAuthenticator
import com.google.android.fhir.sync.remote.HttpLogger
import com.google.firebase.auth.FirebaseAuth
import com.laul.trackaid.R
import com.laul.trackaid.connection.FhirDataStore

class SignInActivity : ComponentActivity() {
    val TAG = "TrackAid_"

    // FIREBASE VARIABLES
    // Firebase instance variables
    private val signIn: ActivityResultLauncher<Intent> = registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

    // FHIR VARIABLES
    // Only initiate the FhirEngine when used for the first time, not when the app is created.
    private val baseUrl = "https://us-central1-trackaid.cloudfunctions.net/fhir-datastore-proxy/"
    val fhirEngine: FhirEngine by lazy { constructFhirEngine() }
    private var dataCaptureConfig: DataCaptureConfig? = null
    private val dataStore by lazy { FhirDataStore(this) }

    // On Start
    public override fun onStart(){
        super.onStart()
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.drawable.ic_pill)
            .setAvailableProviders(
                listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                )
            )
            .build()
        signIn.launch(signInIntent)


    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Sign in successful!")
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true) // Force refresh the token if needed
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token ?: ""
//                        initializeFhirEngine(token)
                        // Use the token for your FHIR server authentication (see previous response)
                        Log.i("FirebaseToken", "Token: $token")
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // Handle token retrieval failure (e.g., network error)
                        Log.w("FirebaseToken", "Failed to get token", task.exception)
                    }
                }



        } else {
            Toast.makeText(
                this,
                "There was an error signing in",
                Toast.LENGTH_LONG).show()

            val response = result.idpResponse
            if (response == null) {
                Log.w(TAG, "Sign in canceled")
            } else {
                Log.w(TAG, "Sign in error", response.error)
            }
        }
    }


    fun initializeFhirEngine(token: String){
        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = true,
                DatabaseErrorStrategy.RECREATE_AT_OPEN,
                ServerConfiguration(
                    baseUrl= baseUrl ,
                    authenticator = HttpAuthenticator { HttpAuthenticationMethod.Bearer(token) },
                    httpLogger =
                    HttpLogger(
                        HttpLogger.Configuration(
                            HttpLogger.Level.BODY // currently buildConfig==DEBUG else HttpLogger.Level.BASIC,
                        ),
                    ) {
                        Log.i("TrackAid_App-HttpLog", it)
                    },
                    networkConfiguration = NetworkConfiguration(uploadWithGzip = false),
                ),
            ),
        )
        dataCaptureConfig =
            DataCaptureConfig().apply {
                urlResolver = ReferenceUrlResolver(this@SignInActivity as Context)
                xFhirQueryResolver = XFhirQueryResolver { it -> fhirEngine.search(it).map { it.resource } }
            }


    }

    // Create FHIR engine instance
    private fun constructFhirEngine(): FhirEngine {
        return FhirEngineProvider.getInstance(this)
    }

    companion object {
        fun fhirEngine(context: Context) = (context.applicationContext as SignInActivity).fhirEngine
        fun dataStore(context: Context) = (context.applicationContext as SignInActivity).dataStore

    }
}