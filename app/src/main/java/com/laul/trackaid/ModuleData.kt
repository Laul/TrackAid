package com.laul.trackaid

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.data.HealthFields
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.laul.trackaid.DataGeneral.Companion.getDate
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

data class ModuleData(
    val mId: Int,
    val mName: String,
    val mUnit: String,
    val mIcon: Int,
    val mColor_Primary: Int,
    val mColor_Secondary: Int,
    val gFitDataType: DataType,
    val gFitOptions: FitnessOptions
    )
{
    var dPoints = ArrayList<LDataPoint>()


    /** GFit connection to retrieve fit data
     * @param duration: duration to cover (default: last 7 days)
     */
    fun getGFitData(permission : Boolean, context: Context,lastCall: MutableState<Long>, time_start: Long, time_end: Long) {
        var gFitReq = DataReadRequest.Builder()
            .aggregate(gFitDataType)
            .bucketByTime(1, TimeUnit.HOURS)
            .setTimeRange(time_start, time_end, TimeUnit.MILLISECONDS)
            .build()

        if (permission) {
            Fitness.getHistoryClient(
                context,
                GoogleSignIn.getAccountForExtension(context, gFitOptions)
            )
                .readData(gFitReq)
                .addOnSuccessListener { response -> formatDatapoint(response, lastCall ) }
                .addOnFailureListener { response -> Log.i("Response", response.toString()) }
        }
    }
    fun formatDatapoint(response: DataReadResponse,lastCall: MutableState<Long>) {
        var result = 0f
        for (bucket in response.buckets) {

            for (dataSet in bucket.dataSets) {

                // data == 0 if no data is available for a given bucket
                if (dataSet.dataPoints.size == 0) {
                    if (dataSet.dataType == HealthDataTypes.TYPE_BLOOD_PRESSURE) {
                        dPoints.add(LDataPoint(bucket.getStartTime(TimeUnit.MILLISECONDS),bucket.getStartTime(TimeUnit.MILLISECONDS), arrayListOf(0f, 0f)))
                    } else {
                        dPoints.add(LDataPoint(bucket.getStartTime(TimeUnit.MILLISECONDS),bucket.getStartTime(TimeUnit.MILLISECONDS), arrayListOf(0f)))
                    }
                }

                // Get data for each bucket and type
                for (dp in dataSet.dataPoints) {
                    if (dataSet.dataType == DataType.TYPE_STEP_COUNT_DELTA) {
                        dPoints.add(LDataPoint(bucket.getStartTime(TimeUnit.MILLISECONDS),dp.getTimestamp(TimeUnit.MILLISECONDS), arrayListOf(dp.getValue(
                            Field.FIELD_STEPS).asInt().toFloat())))
                        //                            dataHealth.dataAxis.add(LDataAxis(bucket.getStartTime(TimeUnit.MILLISECONDS), getDate(bucket.getStartTime(TimeUnit.MILLISECONDS), "EEE"), ))

                    } else if (dataSet.dataType == HealthDataTypes.TYPE_BLOOD_GLUCOSE) {
                        dPoints.add(LDataPoint(bucket.getStartTime(TimeUnit.MILLISECONDS), dp.getTimestamp(TimeUnit.MILLISECONDS),arrayListOf(dp.getValue(
                            HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL).asFloat())))
                    } else if (dataSet.dataType == DataType.TYPE_HEART_RATE_BPM) {
                        dPoints.add(LDataPoint(bucket.getStartTime(TimeUnit.MILLISECONDS), dp.getTimestamp(TimeUnit.MILLISECONDS),arrayListOf(dp.getValue(
                            Field.FIELD_BPM).asFloat())))
                    } else if (dataSet.dataType == HealthDataTypes.TYPE_BLOOD_PRESSURE) {
                        dPoints.add(LDataPoint(bucket.getStartTime(TimeUnit.MILLISECONDS), dp.getTimestamp(TimeUnit.MILLISECONDS), arrayListOf(dp.getValue(
                            HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat(), dp.getValue(
                            HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat())))

                    }
                }
            }
        }

        var tempDate = ArrayList<String>()
        dPoints.forEach {
            tempDate.add(getDate(it.dateMillis_bucket, "EEE"))
        }
        val distinctDate = tempDate.distinct()


        // Display Graph
        if (distinctDate.size > 1 && dPoints.size > 0 ) {
            dPoints = dPoints.sortedWith(compareBy({ it.dateMillis_bucket }))
                .toCollection(ArrayList<LDataPoint>())
        }

        // Update Call timestamp
        lastCall.value = GregorianCalendar().timeInMillis
        Log.i("lastCall changed", lastCall.toString())



//            // Main View: display everything as columns
//            if (dataHealth.context::class == MainActivity::class) {
//                DisplayData.formatAsColumn(dataHealth)
//            }
//
//            // Detailed view: display week chart as columns and day + preview as lines
//            else {
//                //                    if (dataHealth.mname == "Steps" || dataHealth.mname == "Blood Pressure") {
//                DisplayData.formatAsColumn(dataHealth)
//                //                    } else if (dataHealth.mname == "Heart Rate" || dataHealth.mname == "Blood Glucose") {
//                DisplayData.formatAsLine(dataHealth)
//                //                    }
//            }
//        }
    }



}