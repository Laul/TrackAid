package com.laul.trackaid.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.data.HealthFields
import com.google.android.gms.fitness.result.DataReadResponse
import com.laul.trackaid.LDataPoint
import com.laul.trackaid.data.DataGeneral.Companion.getDate
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.extension.setFieldValue
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
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

    var bottomAxisValues = arrayListOf("")



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
    suspend fun getHealthConnectData(
        client: HealthConnectClient,
        now: LocalDateTime,
        start: LocalDateTime,
        listOfDates: ArrayList<LocalDateTime>
    ) {

        val request = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, now)
        )

        val records = client.readRecords(request).records


        if (mName == "Steps") {
            Log.i("StepsTo", records.toString())
        }

    }


    suspend fun aggregateStepsIntoDays(
        healthConnectClient: HealthConnectClient,
        now: LocalDateTime,
        start: LocalDateTime,
        listOfDates: ArrayList<LocalDateTime>

    ) {
        cFloatEntries_Columns.forEach { item ->
            item.clear()
            for (i in 0 until listOfDates.size) {
                item.add(entryOf(item.size, 0f))
            }
        }

            try {
                val response =
                    healthConnectClient.aggregateGroupByPeriod(
                        AggregateGroupByPeriodRequest(
                            metrics = setOf(StepsRecord.COUNT_TOTAL),
                            timeRangeFilter = TimeRangeFilter.between(start, now),
                            timeRangeSlicer = Period.ofDays(1)
                        )
                    )
                for (result in response) {
                    var idDay = listOfDates.indexOf(result.startTime)
                    // The result may be null if no data is available in the time range
                    val totalSteps = result.result[StepsRecord.COUNT_TOTAL]
                    bottomAxisValues.add(result.startTime.dayOfWeek.name)

                    // Columns from 0 to the total number of steps
                    cFloatEntries_Columns[1][idDay] = cFloatEntries_Columns[1][idDay].withY(totalSteps!!.toFloat()) as FloatEntry
                    Log.i("StepsTotal: ", totalSteps.toString())
                }
            } catch (e: Exception) {
                Log.i("StepsToException:", e.toString())
                // Run error handling here
            }

            cChartModel_Columns = entryModelOf(*cFloatEntries_Columns.toTypedArray())
            lastDPoint!!.value = LDataPoint(0, 0, arrayListOf(1f))
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
        //bottomAxisValues = distinctDate
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
// Clear data
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