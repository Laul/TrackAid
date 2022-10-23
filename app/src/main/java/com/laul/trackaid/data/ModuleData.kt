package com.laul.trackaid.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import co.csadev.kellocharts.model.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.data.HealthFields
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.laul.trackaid.LDataPoint
import com.laul.trackaid.data.DataGeneral.Companion.getDate
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
) {
    // Chart variables
    var kChart_Data = LineChartData(arrayListOf<Line>())
    var kXAxis = Axis()
    var kYAxis = Axis()
    var kCol = ArrayList<Line>()
    var dPoints = ArrayList<LDataPoint>()


    /** GFit connection to retrieve fit data
     * @param duration: duration to cover (default: last 7 days)
     */
    fun getGFitData(
        permission: Boolean,
        context: Context,
        lastCall: MutableState<Long>,
        time_start: Long,
        time_end: Long
    ) {


        // Default request using ".read" - For steps, we need to use ".aggregate"
        var gFitReq = DataReadRequest.Builder()
            .read(gFitDataType)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(time_start, time_end, TimeUnit.MILLISECONDS)
            .build()

        if (mName == "Steps") {
            // Request for past (completed) days / hours
            gFitReq = DataReadRequest.Builder()
                .aggregate(gFitDataType)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(time_start, time_end, TimeUnit.MILLISECONDS)
                .build()
        }

        if (permission) {
            Fitness.getHistoryClient(
                context,
                GoogleSignIn.getAccountForExtension(context, gFitOptions)
            )
                .readData(gFitReq)
                .addOnSuccessListener { response -> formatDatapoint(response, lastCall) }
                .addOnFailureListener { response -> Log.i("Response", response.toString()) }
        }
    }

    fun formatDatapoint(response: DataReadResponse, lastCall: MutableState<Long>) {
        var result = 0f
        for (bucket in response.buckets) {

            for (dataSet in bucket.dataSets) {

                // data == 0 if no data is available for a given bucket
                if (dataSet.dataPoints.size == 0) {
                    if (dataSet.dataType == HealthDataTypes.TYPE_BLOOD_PRESSURE) {
                        dPoints.add(
                            LDataPoint(
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                arrayListOf(0f, 0f)
                            )
                        )
                    } else {
                        dPoints.add(
                            LDataPoint(
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                arrayListOf(0f)
                            )
                        )
                    }
                }

                // Get data for each bucket and type
                for (dp in dataSet.dataPoints) {
                    if (dp.dataType == DataType.TYPE_STEP_COUNT_DELTA) {
                        dPoints.add(
                            LDataPoint(
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getTimestamp(TimeUnit.MILLISECONDS),
                                arrayListOf(
                                    dp.getValue(
                                        Field.FIELD_STEPS
                                    ).asInt().toFloat()
                                )
                            )
                        )
                    } else if (dp.dataType == HealthDataTypes.TYPE_BLOOD_GLUCOSE) {
                        dPoints.add(
                            LDataPoint(
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getTimestamp(TimeUnit.MILLISECONDS),
                                arrayListOf(
                                    dp.getValue(
                                        HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL
                                    ).asFloat()
                                )
                            )
                        )
                    } else if (dp.dataType == DataType.TYPE_HEART_RATE_BPM) {
                        dPoints.add(
                            LDataPoint(
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getTimestamp(TimeUnit.MILLISECONDS),
                                arrayListOf(
                                    dp.getValue(
                                        Field.FIELD_BPM
                                    ).asFloat()
                                )
                            )
                        )
                    } else if (dp.dataType == HealthDataTypes.TYPE_BLOOD_PRESSURE) {
                        dPoints.add(
                            LDataPoint(
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getTimestamp(TimeUnit.MILLISECONDS),
                                arrayListOf(
                                    dp.getValue(
                                        HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC
                                    ).asFloat(), dp.getValue(
                                        HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC
                                    ).asFloat()
                                )
                            )
                        )

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
        if (distinctDate.size > 1 && dPoints.size > 0) {
            dPoints = dPoints.sortedWith(compareBy({ it.dateMillis_bucket }))
                .toCollection(ArrayList<LDataPoint>())
        }

        // Update Call timestamp to force recomposition
        formatAsColumn()
        lastCall.value = GregorianCalendar().timeInMillis

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

    fun getLastData(): LDataPoint {
        for (i in dPoints.size - 1 downTo 0) {
            if (!dPoints[i].value.last().isNaN() && dPoints[i].value.sum() > 0) {
                return dPoints[i]
            }
        }
        return LDataPoint(0, 0, arrayListOf(0f))
    }


    fun formatAsColumn() {
        // Clear all lines
        kCol.clear()

        // Group data per Day
        var tempVal = arrayListOf(dPoints[0].value)
        var currentDate = dPoints[0].dateMillis_bucket

        for (i in 1 until dPoints.size) {
            var tempDate = dPoints[i].dateMillis_bucket


            if (getDate(currentDate, "EEE") != getDate(tempDate, "EEE")) {

                // if steps: we aggregate data for a day
                if (mName == "Steps") {
                    computeStepsData(tempVal, currentDate)
                }

                // else: calculate the mean, max, and min per day
//                else {
//                    computeOtherData(dH, tempVal, currentDayMilli)
//                }
                currentDate = tempDate
                //currentDayMilli = dPoints[i].dateMillis_bucket
                tempVal = arrayListOf(dPoints[i].value)
            } else {
                tempVal.add(dPoints[i].value)
            }
        }

        if (kCol.size >2) {
            formatChart(kCol)

        }
    }


    /** Create lines to display steps. Must be the total of steps per day
     */
    fun computeStepsData(tempVal: ArrayList<ArrayList<Float>>, currentDayMilli: Long) {
        var tempValDay = 0f
        for (j in 0 until tempVal.size) {
            tempValDay += tempVal[j][0]
        }

        kCol.add(
            Line(
                arrayListOf(
                    PointValue(currentDayMilli.toFloat(), 0f, ""),
                    PointValue(currentDayMilli.toFloat(), tempValDay, tempValDay.toString()),
                )
            )
        )
    }


    fun formatChart(kCol : ArrayList<Line>) {
        kXAxis.values.clear()
        if (dPoints.size > 0) {

            var kDateEEE = arrayListOf<String>()

            // Create distinct Xaxis string labels based on the date
            for (i in 0 until dPoints.size) {
                kDateEEE.add(getDate(dPoints[i].dateMillis_bucket.toLong(), "EEE"))
            }

            // Get index of each distinct value in the list of string dates
            var kXAxisIndex = ArrayList<Int>()
            if (kDateEEE.distinct().size > 1) {
                kDateEEE.distinct().forEach {
                    kXAxisIndex.add(kDateEEE.indexOf(it))
                }
            }

            // Create axis values
            for (i in 0 until kXAxisIndex.size) {
                kXAxis.values.add(
                    AxisValue(
                        dPoints[kXAxisIndex[i]].dateMillis_bucket.toFloat(),
                        kDateEEE.distinct()[i].toCharArray()
                    )
                )
            }
            kChart_Data = LineChartData(ArrayList<Line>(kCol))


        }
    }
}