package com.laul.trackaid.connection

import android.content.Context
import android.util.Log

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.laul.trackaid.data.DataGeneral
import com.laul.trackaid.data.DataProvider
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class BloodGlucoseUpdate {
    companion object {
        /** XDrip permissions verification and dispatch
         * @param context: App Context (typically main activity)
         */
        fun connectXDrip(context: Context) {
            var mRequestQueue: RequestQueue? = null
            var mStringRequest: StringRequest? = null

            //RequestQueue initialized
            mRequestQueue = Volley.newRequestQueue(context)

            //String Request initialized
            mStringRequest = StringRequest(
                Request.Method.GET,
                "http://127.0.0.1:17580/api/v1/entries/sgv.json" + "?count=" + 100,
                { response ->
                    run {
                        getGlucoData(response, context)
                    }
                }) { error ->
                Log.i("XDrip", "Unable to connect to XDrip: " + error.toString())
            }
            mRequestQueue!!.add(mStringRequest)
        }

        /** Gluco data parsing + formatting to push to Google
         * @param jsonstring: XDrip json string retrieved from connectXDrip
         * @param context: App Context (typically main activi
         */
        fun getGlucoData(jsonstring: String, context: Context) {
            // parse gluco data
            val json = JSONArray(jsonstring)

            // Create DataSource
            val gFitGlucoDSource = DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
                .setType(DataSource.TYPE_RAW)
                .build()

            // Create dataset
            val gFitGlucoDSet = DataSet.builder(gFitGlucoDSource)
            // Loop to get BG data
            for (i in 0 until json.length()) {
                // Get one set of data from JSON
                var measure = json.getJSONObject(i)

                // Add new datapoint to dataset
                gFitGlucoDSet.add(
                    DataPoint.builder(gFitGlucoDSource)
                        .setTimestamp(measure.getLong("date"), TimeUnit.MILLISECONDS)
                        .setField(
                            HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL,
                            (measure.getDouble("sgv") / 18).toFloat()
                        )
                        .build()
                )
            }

            // Request dataset update
            val request = DataUpdateRequest.Builder()
                .setDataSet(gFitGlucoDSet.build())
                .setTimeInterval(
                    json.getJSONObject(99).getLong("date"),
                    json.getJSONObject(0).getLong("date"),
                    TimeUnit.MILLISECONDS
                )
                .build()

            val moduleBG =
                DataProvider.moduleList.values.toList().firstOrNull() { it.mName == "Glucose" }

            if (moduleBG != null) {
//
//                // ---------------------
//                Fitness.getHistoryClient(
//                    context,
//                    GoogleSignIn.getAccountForExtension(
//                        context,
//                        moduleBG.gFitOptions!!
//                    )
//                )
//                    .updateData(request)
//                    .addOnSuccessListener {
//
//                        // Clear dPoints to update the graph (avoid adding data in the graph, we need to completely clear it)
//                        moduleBG.dPoints.clear()
//                        moduleBG.cFloatEntries_Columns.forEach{ item -> item.clear()}
//                        moduleBG.cFloatEntries_Lines.forEach{ item -> item.clear()}
//
//                        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration = moduleBG.duration )
//
//                        moduleBG.getHealthConnectData(
//                            permission = true,
//                            context = context,
//                            time_start = Time_Start,
//                            time_end = Time_End
//                        )
//                        moduleBG.getHealthConnectData(
//                            permission = true,
//                            context = context,
//                            time_start = Time_End,
//                            time_end = Time_Now
//                        )
//
//
//
//                        Log.i("XDrip", "Data update was successful.") }
//                    .addOnFailureListener { e ->
//                        Log.e("XDrip", "There was a problem updating the dataset.", e)
//                    }
//

            }
        }

    }
}

