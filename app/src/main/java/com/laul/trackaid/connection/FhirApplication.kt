package com.laul.trackaid.connection

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.sync.remote.HttpLogger

class FhirApplication : Application() {
    // Only initiate the FhirEngine when used for the first time, not when the app is created.
    private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

    private val baseUrl = "https://northamerica-northeast1-trackaid.cloudfunctions.net/fhir-datastore-proxy"

    override fun onCreate() {
        super.onCreate()

        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = true,
                DatabaseErrorStrategy.RECREATE_AT_OPEN,
                ServerConfiguration(
                    baseUrl= baseUrl ,
                    httpLogger =
                    HttpLogger(
                        HttpLogger.Configuration(
                            HttpLogger.Level.BODY // currently buildConfig==DEBUG else HttpLogger.Level.BASIC,
                        ),
                    ) {
                        Log.i("App-HttpLog", it)
                    },
                    networkConfiguration = NetworkConfiguration(uploadWithGzip = false),
                ),
            ),
        )

    }

        // Create FHIR engine instance
    private fun constructFhirEngine(): FhirEngine {
        return FhirEngineProvider.getInstance(this)
    }

    companion object {
        fun fhirEngine(context: Context) =
            (context.applicationContext as FhirApplication).fhirEngine
    }
}