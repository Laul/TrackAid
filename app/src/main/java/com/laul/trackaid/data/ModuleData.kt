package com.laul.trackaid.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.aggregate.AggregationResultGroupedByDuration
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.laul.trackaid.LDataPoint
import com.laul.trackaid.LDataStats
import com.laul.trackaid.data.DataGeneral.Companion.createTimes
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
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
    var stats: MutableState<LDataStats>?,
    var duration: Int,
    var chartType: String?,
    var nCol: Int,
    var nLines : Int,

    var recordType : KClass<out Record>?,

    ) {
    // Chart variables
    var cFloatEntries_DailyMinMax = arrayListOf<ArrayList<FloatEntry>>()
    var cFloatEntries_DailyAvg = arrayListOf<ArrayList<FloatEntry>>()
    var cFloatEntries_Records = arrayListOf<ArrayList<FloatEntry>>()

    var bottomAxisValues = ArrayList<String>()
    var startAxisValues = ArrayList<Double>()

    // Init variables to set the size of the buckets.
    init {

        if (nCol>0) {
            for (i in 0 until nCol ) {
                cFloatEntries_DailyMinMax.add(arrayListOf())
            }
        }
        if (nLines>0) {
            for (i in 0 until nLines ) {
                cFloatEntries_DailyAvg.add(arrayListOf())
            }
        }

        cFloatEntries_Records.add(arrayListOf())


    }


    /** Get glucose data from HealthConnect
     * @param healthConnectClient: client to retrieve healthconnect data
     */
    suspend fun getGlucoseData(healthConnectClient: HealthConnectClient){
        val (start, now) = createTimes(duration)

        val request = ReadRecordsRequest(
            recordType = BloodGlucoseRecord::class,
            pageSize = 3000,
            timeRangeFilter = TimeRangeFilter.between(start, now)
        )
        val response = healthConnectClient.readRecords(request)
        val records = response.records

        val listOfDates = createDateList(start.atZone(ZoneId.systemDefault()))
        var currentDay = start.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
        val listOfValues = arrayListOf<Double>()

        for (record in records) {
            cFloatEntries_Records[0].add(FloatEntry(record.time.toEpochMilli().toFloat(), record.level.inMillimolesPerLiter.toFloat()))

            if (record.time.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS) != currentDay ) {
                if ( listOfValues.isNotEmpty() ) {
                    aggregateGlucoseData( currentDay, listOfDates, listOfValues)
                    listOfValues.clear()
                }
                currentDay = record.time.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)


            }
            listOfValues.add(record.level.inMillimolesPerLiter)

        }
        if ( listOfValues.isNotEmpty() ) {
            aggregateGlucoseData( currentDay, listOfDates, listOfValues)
        }

        lastDPoint!!.value = getLastData(healthConnectClient)
        stats!!.value = getStats()

    }


    /** Aggregate Gluco data per day to get min, max and average and format for graph
     * @param currentDate: Date of the current day
     * @param listOfDates: list of dates based on the duration
     * @param listOfValues: list of values of the current day
     */
    private fun aggregateGlucoseData(currentDate: ZonedDateTime, listOfDates: ArrayList<ZonedDateTime>, listOfValues : ArrayList<Double>){
//        val idDay = listOfDates.indexOf(currentDate)
        val idDay = (0 until listOfDates.size).firstOrNull { listOfDates[it].dayOfYear == currentDate.dayOfYear }

        if( idDay != null ) {
            cFloatEntries_DailyMinMax[0][idDay] =
                cFloatEntries_DailyMinMax[0][idDay].withY(
                    listOfValues.min().toFloat()
                ) as FloatEntry
            cFloatEntries_DailyMinMax[1][idDay] =
                cFloatEntries_DailyMinMax[1][idDay].withY(
                    listOfValues.max().toFloat() -listOfValues.min().toFloat()
                ) as FloatEntry
            cFloatEntries_DailyAvg[0][idDay] =
                cFloatEntries_DailyAvg[0][idDay].withY(
                    listOfValues.average().toFloat()
                ) as FloatEntry
        }
    }


    /** Get all HealthConnect data types except glucose
     * @param healthConnectClient: client to retrieve healthconnect data
     */
    suspend fun getHealthConnectData(
        healthConnectClient: HealthConnectClient,
        durationSlicer : Duration
    ) {
        val (start, now) = createTimes(duration)

       var response = listOf<AggregationResultGroupedByDuration>()
       var metrics = setOf<AggregateMetric<*>>()

       if (mName == "Steps") {metrics = setOf(StepsRecord.COUNT_TOTAL)}
       if (mName == "Heart Rate") {metrics =setOf(HeartRateRecord.BPM_AVG, HeartRateRecord.BPM_MIN, HeartRateRecord.BPM_MAX)}
       if (mName == "Pressure") {metrics = setOf(BloodPressureRecord.DIASTOLIC_AVG, BloodPressureRecord.SYSTOLIC_AVG)}

       try {
           response =
                healthConnectClient.aggregateGroupByDuration(
                    AggregateGroupByDurationRequest(
                        metrics = metrics,
                        timeRangeFilter = TimeRangeFilter.between(start, now),
                        timeRangeSlicer = durationSlicer
                    )
                )
        }

        catch (e: Exception) {
            Log.i("StepsToException:", e.toString())
            // Run error handling here
        }

        formatData(start.atZone(ZoneId.systemDefault()), response)
        lastDPoint!!.value = getLastData(healthConnectClient)
        stats!!.value = getStats()

    }


    /** Create the list of dates to get each day based on the duration
     * @param start: start date based on the duration
     */
    private fun createDateList(start: ZonedDateTime) : ArrayList<ZonedDateTime> {
        // Create a list of dates to assign proper values to days
        val listOfDates = arrayListOf<ZonedDateTime>()
        for (i in 0 until duration + 1) {
            listOfDates.add(start.plus(i.toLong(), ChronoUnit.DAYS))
        }
        listOfDates.forEach {
            bottomAxisValues.add(it.format(DateTimeFormatter.ofPattern("EEE")))
        }

        // Clear data variable for chart
        cFloatEntries_DailyMinMax.forEach { item ->
            item.clear()
            for (i in 0 until listOfDates.size) {
                item.add(entryOf(item.size, 0f))
            }
        }
        cFloatEntries_DailyAvg.forEach { item ->
            item.clear()
            for (i in 0 until listOfDates.size) {
                item.add(entryOf(item.size, 0f))
            }
        }
        return listOfDates
    }

    /** Health Connect data formatting for display on graph
     * @param start:  Date of start of daily values (between start and now)
     * @param response: response of aggregated data from health connect
     */
    private fun formatData(start: ZonedDateTime, response: List<AggregationResultGroupedByDuration>) {
        val listOfDates = createDateList(start)


        for (result in response) {
//            val idDay = listOfDates.indexOf(result.startTime.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS))
            val idDay = (0 until listOfDates.size).firstOrNull { listOfDates[it].dayOfYear == result.startTime.atZone(ZoneId.systemDefault()).dayOfYear }

            if( idDay != null ) {
                when (mName) {
                    "Steps" -> {
                        // Columns from 0 to the total number of steps
                        cFloatEntries_DailyMinMax[0][idDay] =
                            cFloatEntries_DailyMinMax[0][idDay].withY(result.result[StepsRecord.COUNT_TOTAL]!!.toFloat()) as FloatEntry
                    }

                    "Heart Rate" -> {
                        // Columns from min to max + average as point
                        cFloatEntries_DailyMinMax[0][idDay] =
                            cFloatEntries_DailyMinMax[0][idDay].withY(result.result[HeartRateRecord.BPM_MIN]!!.toFloat()) as FloatEntry
                        cFloatEntries_DailyMinMax[1][idDay] =
                            cFloatEntries_DailyMinMax[1][idDay].withY(result.result[HeartRateRecord.BPM_MAX]!!.toFloat()) as FloatEntry
                        cFloatEntries_DailyAvg[0][idDay] =
                            cFloatEntries_DailyAvg[0][idDay].withY(result.result[HeartRateRecord.BPM_AVG]!!.toFloat()) as FloatEntry
                    }

                    "Pressure" -> {
                        // Columns from min to max + average as point
                        if (result.result[BloodPressureRecord.DIASTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - result.result[BloodPressureRecord.DIASTOLIC_MIN]!!.inMillimetersOfMercury.toFloat() > 10f) {
                            cFloatEntries_DailyMinMax[0][idDay] =
                                cFloatEntries_DailyMinMax[0][idDay].withY(result.result[BloodPressureRecord.DIASTOLIC_MIN]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                            cFloatEntries_DailyMinMax[1][idDay] =
                                cFloatEntries_DailyMinMax[1][idDay].withY(result.result[BloodPressureRecord.DIASTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - cFloatEntries_DailyMinMax[0][idDay].y) as FloatEntry
                        }
                        if (result.result[BloodPressureRecord.SYSTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - result.result[BloodPressureRecord.SYSTOLIC_MIN]!!.inMillimetersOfMercury.toFloat() > 10f) {
                            cFloatEntries_DailyMinMax[0][idDay] =
                                cFloatEntries_DailyMinMax[2][idDay].withY(result.result[BloodPressureRecord.SYSTOLIC_MIN]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                            cFloatEntries_DailyMinMax[1][idDay] =
                                cFloatEntries_DailyMinMax[3][idDay].withY(result.result[BloodPressureRecord.SYSTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - cFloatEntries_DailyMinMax[0][idDay].y) as FloatEntry
                        }

                        cFloatEntries_DailyAvg[0][idDay] =
                            cFloatEntries_DailyAvg[0][idDay].withY(result.result[BloodPressureRecord.DIASTOLIC_AVG]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                        cFloatEntries_DailyAvg[1][idDay] =
                            cFloatEntries_DailyAvg[1][idDay].withY(result.result[BloodPressureRecord.SYSTOLIC_AVG]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                    }

                }
            }

        }
    }



    /** Last data available
     * @param healthConnectClient: client to healthconnect
     */
    suspend fun getLastData(healthConnectClient: HealthConnectClient): LDataPoint {
        val (start, now) = createTimes(duration)

        val request = ReadRecordsRequest(
            recordType = recordType!!,
            timeRangeFilter = TimeRangeFilter.between(start, now),
            ascendingOrder = false,
            pageSize = 3000
        )

        val response = healthConnectClient.readRecords(request)
        val records = response.records

        val value = arrayListOf<Float>()
        var date =ZonedDateTime.now()

        when (mName) {
            "Steps" -> {
                value.add(cFloatEntries_DailyMinMax[0].last().y)
                date = now.atZone(ZoneId.systemDefault())
            }
            "Glucose" -> {
                value.add((records[0] as BloodGlucoseRecord).level.inMillimolesPerLiter.toFloat())
                date = ZonedDateTime.ofInstant((records[0] as BloodGlucoseRecord).time, java.time.ZoneId.systemDefault())
            }
            "Heart Rate" -> {
                for (record in records) {
                    cFloatEntries_Records[0].add(FloatEntry((record as HeartRateRecord).endTime.toEpochMilli().toFloat(), record.samples.stream().mapToLong{it.beatsPerMinute}.summaryStatistics().average.toFloat()))
                }
                value.add((records[0] as HeartRateRecord).samples.stream().mapToLong{it.beatsPerMinute}.summaryStatistics().average.toFloat())
                date = ZonedDateTime.ofInstant((records[0] as HeartRateRecord).endTime, java.time.ZoneId.systemDefault())
            }
        }
        return LDataPoint(date.format(DateTimeFormatter.ofPattern("E, MMM dd hh:mm a")),  value)
    }


    private fun getStats(): LDataStats{

        var avg = 0f
        var min = 0f
        var max = 0f

        // If the chart is a combo of lines and columns:
        // - Lines contain info about average
        // - Columns contain info about min (first arraylist) and max (second arraylist)
        if (chartType == "Combo"){
            // Min and avg are the min of all min
            min = cFloatEntries_DailyMinMax[0].stream().filter { it.y != 0f }.mapToDouble { it.y.toDouble() }.summaryStatistics().min.toFloat()
            avg = cFloatEntries_DailyAvg[0].stream().filter { it.y != 0f }.mapToDouble { it.y.toDouble() }.summaryStatistics().average.toFloat()

            // Max of the week is the max of all max. Cannot be based on the columns because of stacking
            for (i in 0 until duration ) {
                var dailyMax = cFloatEntries_DailyMinMax[0][i].y + cFloatEntries_DailyMinMax[1][i].y
                if (dailyMax>max) max = dailyMax

            }

        }

        if (chartType == "Columns"){
            min = cFloatEntries_DailyMinMax[0].stream().filter{it.y !=0f}.mapToDouble{it.y.toDouble()}.summaryStatistics().min.toFloat()
            max = cFloatEntries_DailyMinMax[0].stream().filter{it.y !=0f}.mapToDouble{it.y.toDouble()}.summaryStatistics().max.toFloat()
            avg = cFloatEntries_DailyMinMax[0].stream().filter{it.y !=0f}.mapToDouble{it.y.toDouble()}.summaryStatistics().average.toFloat()
        }


        return  LDataStats( min, max, avg )

    }


}
