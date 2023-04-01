package com.laul.trackaid.data
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import co.csadev.kellocharts.model.*
import co.csadev.kellocharts.view.AbstractChartView
import co.csadev.kellocharts.view.LineChartView
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
    val mUnit: String?,
    val mIcon: Int,
    val mIcon_outlined : Int,
    val mColor_Primary: Int?,
    val mColor_Secondary: Int?,
    val gFitDataType: DataType?,
    val gFitOptions: FitnessOptions?,
    var lastDPoint : MutableState<LDataPoint>?,
    var duration: Int
) {
    // Chart variables
    var kChart_Data = LineChartData(arrayListOf<Line>())
    var kXAxis = Axis()
    var kYAxis = Axis()
    var kCol = ArrayList<Line>()
    var dPoints = ArrayList<LDataPoint>()

    // Views to plot graph and add values
    var kChartView_Week: AbstractChartView? = null
    var kChartView_Day: AbstractChartView? = null
    var kChartView_PreviewWeek: AbstractChartView? = null
    var daysOfWeek  = ArrayList<Long>()
    var daysOfWeek_str  = ArrayList<String>()


    // Initialize graph data to avoid problems if no data is available for the past {duration} days in GFit buckets
    init {
        var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)
//        var week = arrayListOf<>()
        daysOfWeek.add(Time_Start)
        for (i in 0 until (duration-1))
        {
            daysOfWeek.add(daysOfWeek[i]+((Time_End - Time_Start)/(duration-1)))
        }

        for (i in 0 until daysOfWeek.size) {
            daysOfWeek_str.add(getDate(daysOfWeek[i], "yyyy-MM-dd'T'HH:mm:ss"))
        }

//
        // Create axis values
        for (i in 0 until daysOfWeek.size) {
            kXAxis.values.add(
                AxisValue(
                    daysOfWeek[i].toFloat(),
                    getDate(daysOfWeek[i], "EEE").toCharArray()
                )
            )

            kCol.add(
                Line(
                    arrayListOf(
                        PointValue(daysOfWeek[i].toFloat(), 0.toFloat(), ""),
                        PointValue(daysOfWeek[i].toFloat(), 0.toFloat(), ""),
                    )
                )
            )
        }
        kChart_Data = LineChartData(ArrayList<Line>(kCol))

        kCol.forEach {
//            it.color = mColor_Primary!!
            it.isSquare = true
            it.hasLabelsOnlyForSelected = true
            it.isFilled = true
            it.hasPoints = false
            it.strokeWidth = 8
            it.pointRadius = 0
            it.hasLabels = false
        }




    }


    /** GFit connection to retrieve fit data
     * @param duration: duration to cover (default: last 7 days)
     */
    fun getGFitData(
        permission: Boolean,
        context: Context,
        time_start: Long,
        time_end: Long
    ) {


        // Default request using ".read" - For steps, we need to use ".aggregate"

        var gFitReq = DataReadRequest.Builder()
            .read(gFitDataType!!)
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

        if (mName == "Pressure")
        {
            Log.i("GFit Req pressure", gFitReq.toString())
        }
        if (permission) {
            Fitness.getHistoryClient(
                context,
                GoogleSignIn.getAccountForExtension(context, gFitOptions!!)
            )
                .readData(gFitReq)
                .addOnSuccessListener { response -> formatDatapoint(response ) }
                .addOnFailureListener { response -> Log.i("Response", response.toString()) }
        }
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
        lastDPoint!!.value = getLastData()


    //        lastValue = lastDPoint.value

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
        return LDataPoint(0, 0, arrayListOf(0f, 0f))
    }


    fun formatAsColumn() {
        if (dPoints.size != 0) {
            // Clear all lines
            kCol.clear()

            // Group data per Day
            var tempVal = arrayListOf(dPoints[0].value)
            var currentDate = dPoints[0].dateMillis_bucket

            for (i in 1 until dPoints.size) {
                var tempDate = dPoints[i].dateMillis_bucket


                if (currentDate != tempDate) {

                    // if steps: we aggregate data for a day
                    if (mName in arrayOf( "Steps") ) {
                        computeStepsData(tempVal, currentDate)
                    }

                    // else: calculate the mean, max, and min per day
                    else {
                        computeOtherData(tempVal, currentDate)
                    }
                    currentDate = tempDate
                    //currentDayMilli = dPoints[i].dateMillis_bucket
                    tempVal = arrayListOf(dPoints[i].value)
                } else {
                    tempVal.add(dPoints[i].value)
                }
            }



            // if steps: we aggregate data for a day
            if (mName == "Steps") {
                computeStepsData(tempVal, currentDate)
            }

            // else: calculate the mean, max, and min per day
            else {
                computeOtherData(tempVal, currentDate)
            }

//            if (kCol.size > 2) {
                formatChart(kCol)

//            }
        }
    }


    /** Create lines to display steps. Must be the total of steps per day
     */
    fun computeStepsData(tempVal: ArrayList<ArrayList<Float>>, currentDate: Long) {
        var tempValDay = 0f
        for (j in 0 until tempVal.size) {
            tempValDay += tempVal[j][0]
        }

        kCol.add(
            Line(
                arrayListOf(
                    PointValue(currentDate.toFloat(), 0f, ""),
                    PointValue(currentDate.toFloat(), tempValDay, tempValDay.toString()),
                )
            )
        )
    }

    /** Create lines to display everything except steps.
    * 2 cases:
    *      - Blood Glucose + Heart Rate : min, max,mean - we display min and max
    *      - Blood Pressure : display mean of diastol + systol
    */
    fun computeOtherData(tempVal: ArrayList<ArrayList<Float>>, currentDate: Long) {
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
        // Create a line for a given day
        if (mName == "Pressure") {
            kCol.add(
                Line(
                    arrayListOf(
                        PointValue(currentDate.toFloat(), tempValMean[0], tempValMean[0].toString()),
                        PointValue(currentDate.toFloat(), tempValMean[1], tempValMean[1].toString()),
                    )
                )
            )
        }
        else  {
            kCol.add(
                Line(
                    arrayListOf(
                        PointValue(currentDate.toFloat(), tempValMin[0], tempValMin[0].toString()),
                        PointValue(currentDate.toFloat(), tempValMax[0], tempValMax[0].toString()),
                    )
                )
            )
        }


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
            if (kDateEEE.distinct().size > 0) {
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

            // Create a LineChartData using time and Line data
            kChart_Data = LineChartData(ArrayList<Line>(kCol))


            // Add axis values and push it in the chart + formatting
            kXAxis.textColor = mColor_Primary!!
            kYAxis.textColor = mColor_Primary!!

//            if( context::class == MainActivity::class) {e
                kXAxis.name = mName
                kChart_Data.axisYRight = kYAxis
//            }
//            else{
//                dH.kXAxis.name = ""
//                kChart_Data.axisYLeft = dH.kYAxis
//            }
//
            kChart_Data.axisXBottom = kXAxis

//
//            if (isWeek){
//                (kChartView_Week as LineChartView).lineChartData = kChart_Data
//            }
//            else{
//                (dH.kChartView_Day as LineChartView).lineChartData = kChart_Data
//            }
//            val tempViewport = kChartView_Week?.maximumViewport.copy()
//            val tempPreViewport = tempViewport.copy()

//            // If in main activity, add an inset to have the entire labels for axis
//            tempViewport.inset(-tempViewport.width() * 0.05f, -tempViewport.height() * 0.05f)
//            dH.kChartView_Week?.maximumViewport = tempViewport
//            dH.kChartView_Week?.currentViewport = tempViewport


            // Format Chart
            kCol.forEach {

                it.isSquare = true
                it.hasLabelsOnlyForSelected = true
                it.isFilled = true
                it.hasPoints = false
                it.strokeWidth = 8
//                it.color = mColor_Primary
                it.pointRadius = 0
                it.hasLabels = false
            }
        }
    }
}