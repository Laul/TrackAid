package com.laul.trackaid.data

import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.Record

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
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

data class ModuleData(
    val mId: Int,
    val mName: String,
    val mUnit: String?,
    val mIcon: Int,
    val mIcon_outlined: Int,
    val mColor_Primary: Int?,
    val mColor_Secondary: Int?,
    var lastDPoint: MutableState<LDataPoint>?,
    var duration: Int,
    var chartType: String?,
    var nCol: Int,
    var nLines : Int,

    var recordType : KClass<out Record>?,

) {
    // Chart variables
    var dPoints = ArrayList<LDataPoint>()
    var cFloatEntries_Columns = arrayListOf<ArrayList<FloatEntry>>()
    var cFloatEntries_Lines = arrayListOf<ArrayList<FloatEntry>>()
    var cChartModel_Columns = entryModelOf(*cFloatEntries_Columns.toTypedArray())
    var cChartModel_Lines = entryModelOf(*cFloatEntries_Lines.toTypedArray())
    var bottomAxisValues = listOf("")



    // Init variables
    init {
        if (nCol>0) {
            for (i in 0 until nCol+1 ) {
                cFloatEntries_Columns.add(arrayListOf<FloatEntry>())
            }
        }
        if (nLines>0) {
            for (i in 0 until nCol ) {
                cFloatEntries_Lines.add(arrayListOf<FloatEntry>())
            }
        }
    }



    /** GFit connection to retrieve fit data
     * @param duration: duration to cover (default: last 7 days)
     */
    fun getHealthConnectData(
        context: Context,
        time_start: Long,
        time_end: Long
    ) {

//
//        // Default request using ".read" - For steps, we need to use ".aggregate"
//
//        var gFitReq = DataReadRequest.Builder()
//            .read(healthConnectDataType!!)
//            .bucketByTime(1, TimeUnit.DAYS)
//            .setTimeRange(time_start, time_end, TimeUnit.MILLISECONDS)
//            .build()
//
//        if (mName == "Steps") {
//            // Request for past (completed) days / hours
//            gFitReq = DataReadRequest.Builder()
//                .aggregate(healthConnectDataType)
//                .bucketByTime(1, TimeUnit.DAYS)
//                .setTimeRange(time_start, time_end, TimeUnit.MILLISECONDS)
//                .build()
//        }
//
//        if (mName == "Heart Rate") {
//            Log.i("Heart Rate Req", gFitReq.toString())
//        }
//        if (permission) {
//            Fitness.getHistoryClient(
//                context,
//                GoogleSignIn.getAccountForExtension(context, gFitOptions!!)
//            )
//                .readData(gFitReq)
//                .addOnSuccessListener { response -> formatDatapoint(response) }
//                .addOnFailureListener { response -> Log.i("Response", response.toString()) }
//        }
    }


    fun formatDatapoint(response: DataReadResponse) {
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
                    } else if (dp.dataType == DataType.TYPE_WEIGHT) {
                        dPoints.add(
                            LDataPoint(
                                bucket.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getTimestamp(TimeUnit.MILLISECONDS),
                                arrayListOf(
                                    dp.getValue(
                                        Field.FIELD_WEIGHT
                                    ).asFloat()
                                )
                            )
                        )
                    }
                }
            }
        }

        var tempDate = ArrayList<String>()


        dPoints = dPoints.sortedWith(compareBy({ it.dateMillis_bucket }))
            .toCollection(ArrayList<LDataPoint>())

        dPoints.forEach {
            tempDate.add(getDate(it.dateMillis_bucket, "EEE"))
        }
        val distinctDate = tempDate.distinct()

        // Update Call timestamp to force recomposition
        formatDPoints()
        bottomAxisValues = distinctDate
        lastDPoint!!.value = getLastData()

    }

    fun getLastData(): LDataPoint {
        for (i in dPoints.size - 1 downTo 0) {
            if (!dPoints[i].value.last().isNaN() && dPoints[i].value.sum() > 0) {
                return dPoints[i]
            }
        }
        return LDataPoint(0, 0, arrayListOf(0f, 0f))
    }


    fun formatDPoints() {
//
        cFloatEntries_Columns.forEach{ item -> item.clear()}
        cFloatEntries_Lines.forEach{ item -> item.clear()}

        if (dPoints.size != 0) {

            // Group data per Day
            var tempVal = arrayListOf(dPoints[0].value)
            var currentDate = dPoints[0].dateMillis_bucket

            for (i in 1 until dPoints.size) {
                var tempDate = dPoints[i].dateMillis_bucket


                if (currentDate != tempDate) {
                    formatChartModel(tempVal, currentDate)

                    currentDate = tempDate
                    tempVal = arrayListOf(dPoints[i].value)
                } else {
                    tempVal.add(dPoints[i].value)
                }
            }

            formatChartModel(tempVal, currentDate)


        }
    }


    /** Create lines to display steps. Must be the total of steps per day
     */
    fun formatChartModel(tempVal: ArrayList<ArrayList<Float>>, currentDate: Long) {

        // For steps, we aggregate the total number of steps per day
        if (mName == "Steps") {
            var tempValDay = 0f
            for (j in 0 until tempVal.size) {
                tempValDay += tempVal[j][0]
            }

            // Columns from 0 to the total number of steps
            cFloatEntries_Columns[0].add(entryOf(cFloatEntries_Columns[0].size,0f))
            cFloatEntries_Columns[1].add(entryOf(cFloatEntries_Columns[1].size,tempValDay))

        }

        else {
            // Compute the mean of min and max (needed if several values are taken the same day)
            var tempValMean = ArrayList<Float>()
            var tempValMin = ArrayList<Float>()
            var tempValMax = ArrayList<Float>()
            for (k in 0 until tempVal[0].size) {
                tempValMean.add(0f)
                tempValMin.add(10000f)
                tempValMax.add(0f)

                var sizeDay = 0

                for (j in 0 until tempVal.size) {
                    tempValMean[k] += tempVal[j][k]
                    if (tempValMin[k] > tempVal[j][k]) {
                        tempValMin[k] = tempVal[j][k]
                    }
                    if (tempValMax[k] < tempVal[j][k]) {
                        tempValMax[k] = tempVal[j][k]
                    }
                    sizeDay += 1
                }

                tempValMean[k] = tempValMean[k] / sizeDay
            }
            // For pressure, compute the mean of diastole and systole per day and create columns from mean(diastole) to mean(systole)
            if (mName == "Pressure") {
                cFloatEntries_Columns[0].add(entryOf(cFloatEntries_Columns[0].size, tempValMean[0]))
                cFloatEntries_Columns[1].add(entryOf(cFloatEntries_Columns[1].size,tempValMean[1] - tempValMean[0]))
            }

            // For other data, create columns from min and max + points for the daily mean
            else {
                cFloatEntries_Columns[0].add(entryOf(cFloatEntries_Columns[0].size, tempValMin[0]))
                cFloatEntries_Columns[1].add(entryOf(cFloatEntries_Columns[1].size,tempValMax[0] + .1 - tempValMin[0]))

                if (nLines>0) {
                    cFloatEntries_Lines[0].add(entryOf(cFloatEntries_Lines[0].size, tempValMean[0]))
                }
            }
        }

        // Create the model for columns and lines charts
        cChartModel_Columns = entryModelOf(*cFloatEntries_Columns.toTypedArray())
        cChartModel_Lines = entryModelOf(*cFloatEntries_Lines.toTypedArray())

    }

}