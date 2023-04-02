package com.laul.trackaid.connection

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import co.csadev.kellocharts.model.PointValue
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.laul.trackaid.data.DataProvider
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class BloodGlucoseUpdate {
companion object {
    /** XDrip permissions verification and dispatch
     * @param count: number of data to retrieve from XDrip. Max is 1000, XDrip does neither allow nor to specify time interval, nor more than the last 1000 values (~3.5 days of data)
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
     */
    fun getGlucoData(jsonstring: String, context:Context) {
        // parse gluco data
        val json = JSONArray(jsonstring)

        // Initialize an empty variable to aggreagate and format glucose datapoints
        var xDripValues = ArrayList<PointValue>()

        // Loop to get BG data
        for (i in 0 until json.length()) {
            // Get one set of data from JSON
            var measure = json.getJSONObject(i)

            // Get BG values and create associated PointValue
            val sgv = measure.getDouble("sgv")/18
            xDripValues.add(PointValue(measure.getLong("date").toFloat(),sgv.toFloat(), "%.2f".format(sgv)))
        }

        pushGlucoData(xDripValues, context)
    }

    /**
     * Push last 1000 gluco data from XDrip to GFit.
     */
    fun pushGlucoData(xDripValues : ArrayList<PointValue>, context: Context) {
        // Create DataSource
        val gFitGlucoDSource = DataSource.Builder()
            .setAppPackageName(context)
            .setDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
            .setType(DataSource.TYPE_RAW)
            .build()

        // Create dataset
        val gFitGlucoDSet = DataSet.builder(gFitGlucoDSource)

        for (i in 0 until xDripValues.size){
            val date = xDripValues[i].x.toLong()
            val sgv = xDripValues[i].y


            // Add new datapoint to dataset
            gFitGlucoDSet.add(
                DataPoint.builder(gFitGlucoDSource)
                .setTimestamp(date, TimeUnit.MILLISECONDS)
                .setField(HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL, sgv)
                .build()
            )
        }



        // Request dataset update
        val request = DataUpdateRequest.Builder()
            .setDataSet(gFitGlucoDSet.build())
            .setTimeInterval(xDripValues[xDripValues.size -1 ].x.toLong(), xDripValues[0].x.toLong(), TimeUnit.MILLISECONDS)
            .build()

        val moduleBG = DataProvider.moduleList.values.toList().firstOrNull(){it.mName == "Glucose"}

        if( moduleBG != null) {

            // ---------------------
            Fitness.getHistoryClient(
                context,
                GoogleSignIn.getAccountForExtension(
                    context,
                    moduleBG.gFitOptions!!
                )
            )
                .updateData(request)
                .addOnSuccessListener { Log.i("XDrip", "Data update was successful.") }
                .addOnFailureListener { e ->
                    Log.e("XDrip", "There was a problem updating the dataset.", e)
                }

            // Clear dPoints to update the graph
            moduleBG.dPoints.clear()

        }


        // ---------------------




        }



    }
}

