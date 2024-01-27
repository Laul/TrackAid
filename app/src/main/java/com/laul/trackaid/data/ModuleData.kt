package com.laul.trackaid.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.health.connect.client.HealthConnectClient
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
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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
    var thresholdMin : Float,
    var thresholdMax : Float,

    ) {
    // Chart variables
    var cFloatEntries_DailyMinMax = arrayListOf<ArrayList<FloatEntry>>()
    var cFloatEntries_DailyAvg = arrayListOf<ArrayList<FloatEntry>>()
    var cFloatEntries_Records = arrayListOf<ArrayList<FloatEntry>>()

    var bottomAxisValues = ArrayList<String>()
    var bottomAxisValues_Detailed = ArrayList<String>()
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



    /** Get all HealthConnect data types except glucose
     * @param healthConnectClient: client to retrieve healthconnect data
     */
    suspend fun getHealthConnectData(healthConnectClient: HealthConnectClient) {
       if (mName == "Steps") {getStepsData(healthConnectClient)}
       if (mName == "Heart Rate") {getHRData(healthConnectClient)}
       if (mName == "Glucose") {getGlucoseData(healthConnectClient)}
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
        cFloatEntries_Records.forEach { item ->
            item.clear()

        }

        return listOfDates
    }



    /** Get glucose data from HealthConnect
     * @param healthConnectClient: client to retrieve healthconnect data
     */
    suspend fun getGlucoseData(healthConnectClient: HealthConnectClient){
        val (start, now) = createTimes(duration)

        // Request all data to Health Connect
        val response = healthConnectClient.readRecords(ReadRecordsRequest(
            recordType = BloodGlucoseRecord::class,
            pageSize = 3000,
            timeRangeFilter = TimeRangeFilter.between(start, now)
        ))
        val records = response.records

        // Set variables to store data
        val listOfDates = createDateList(start.atZone(ZoneId.systemDefault()))
        var currentDay = start.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
        val listOfValues = arrayListOf<Double>()

        // Loop to get all records in proper variables
        for (record in records) {
            // All Records to display in detailed view
            cFloatEntries_Records[0].add(FloatEntry(record.time.toEpochMilli().toFloat(), record.level.inMillimolesPerLiter.toFloat()))

            if (record.time.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS) != currentDay ) {
                // Aggregate Glucose for main view chart
                if ( listOfValues.isNotEmpty() ) {
                    aggregateGlucoseData( currentDay, listOfDates, listOfValues)
                    listOfValues.clear()
                }
                currentDay = record.time.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)

            }
            listOfValues.add(record.level.inMillimolesPerLiter)

        }

        // Complete aggregation
        if ( listOfValues.isNotEmpty() ) {
            aggregateGlucoseData( currentDay, listOfDates, listOfValues)
        }



        // LAST DATA
        if (records.isNotEmpty() ) {
            val value = arrayListOf(records.last().level.inMillimolesPerLiter.toFloat())
            val date = ZonedDateTime.ofInstant(records.last().time, ZoneId.systemDefault())
            lastDPoint!!.value =
                LDataPoint(date.format(DateTimeFormatter.ofPattern("E, MMM dd hh:mm a")), value)
        }


        // STATS
        stats!!.value = getStats()

    }

    /** Aggregate Gluco data per day to get min, max and average and format for graph
     * @param currentDate: Date of the current day
     * @param listOfDates: list of dates based on the duration
     * @param listOfValues: list of values of the current day
     */
    private fun aggregateGlucoseData(currentDate: ZonedDateTime, listOfDates: ArrayList<ZonedDateTime>, listOfValues : ArrayList<Double>){
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

    /** Get Steps data from HealthConnect
     * @param healthConnectClient: client to retrieve healthconnect data
     */
    suspend fun getStepsData(healthConnectClient: HealthConnectClient) {
        val (start, now) = createTimes(duration)

        //  MAIN VIEW - Aggregated data per day
        try {
            var response =
                healthConnectClient.aggregateGroupByDuration(
                    AggregateGroupByDurationRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, now),
                        timeRangeSlicer = Duration.ofDays(1)
                    )
                )
            formatData(start.atZone(ZoneId.systemDefault()), response)
        }

        catch (e: Exception) {
            Log.i("StepsToException:", e.toString())
            // Run error handling here
        }

        //  DETAILED VIEW - Aggregated data per hour
        // Request all data to Health Connect
        try {
            var response =
                healthConnectClient.aggregateGroupByDuration(
                    AggregateGroupByDurationRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, now),
                        timeRangeSlicer = Duration.ofHours(1)
                    )
                )

            val listOfHours = arrayListOf<ZonedDateTime>()

            for (i in 0 until (duration + 1)*24) {
                listOfHours.add(start.atZone(ZoneId.systemDefault()).plus(i.toLong(), ChronoUnit.HOURS))
            }

            listOfHours.forEach {
                bottomAxisValues_Detailed.add(it.format(DateTimeFormatter.ofPattern("hh:mm")))
            }

            cFloatEntries_Records.forEach { item ->
                for (i in 0 until listOfHours.size) {
                    item.add(entryOf(item.size, 0f))
                }
            }


            for (bucket in response) {
                val idHour = (0 until listOfHours.size).firstOrNull { listOfHours[it].truncatedTo(ChronoUnit.HOURS) == bucket.endTime.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.HOURS) }
                if( idHour != null ) {
                    cFloatEntries_Records[0][idHour] = cFloatEntries_Records[0][idHour].withY(bucket.result[StepsRecord.COUNT_TOTAL]!!.toFloat()) as FloatEntry


//
//                        .add(
//                        FloatEntry(
//                            bucket.endTime.toEpochMilli().toFloat(),
//                            bucket.result[StepsRecord.COUNT_TOTAL]!!.toFloat()
//                        )
//                    )
                }
            }


            //  LAST DATA
            val value = arrayListOf(cFloatEntries_DailyMinMax[0].last().y)
            val date = now.atZone(ZoneId.systemDefault())
            lastDPoint!!.value = LDataPoint(date.format(DateTimeFormatter.ofPattern("E, MMM dd hh:mm a")),  value)

            // STATS
            stats!!.value = getStats()

        }

        catch (e: Exception) {
            Log.i("" +
                    "stepsToException:", e.toString())
            // Run error handling here
        }


    }


    /** Get Heart Rate data from HealthConnect
     * @param healthConnectClient: client to retrieve healthconnect data
     */
    suspend fun getHRData(healthConnectClient: HealthConnectClient) {
        val (start, now) = createTimes(duration)

        //  MAIN VIEW - Aggregated data
        try {
            var response =
                healthConnectClient.aggregateGroupByDuration(
                    AggregateGroupByDurationRequest(
                        metrics = setOf(HeartRateRecord.BPM_AVG, HeartRateRecord.BPM_MIN, HeartRateRecord.BPM_MAX),
                        timeRangeFilter = TimeRangeFilter.between(start, now),
                        timeRangeSlicer = Duration.ofDays(1)
                    )
                )
            formatData(start.atZone(ZoneId.systemDefault()), response)
        }

        catch (e: Exception) {
            Log.i("Heart RateToException:", e.toString())
            // Run error handling here
        }

        //  DETAILED VIEW - All records
        // Request all data to Health Connect
        try {
            val response = healthConnectClient.readRecords(ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                pageSize = 3000,
                timeRangeFilter = TimeRangeFilter.between(start, now)
            ))

        for (record in response.records) {
            cFloatEntries_Records[0].add(FloatEntry(record.endTime.toEpochMilli().toFloat(), record.samples.stream().mapToLong{it.beatsPerMinute}.summaryStatistics().average.toFloat()))
        }

        //  LAST DATA
        val value = arrayListOf(response.records.last().samples.stream().mapToLong{it.beatsPerMinute}.summaryStatistics().average.toFloat())
        val date = ZonedDateTime.ofInstant(response.records.last().endTime,ZoneId.systemDefault())
        lastDPoint!!.value = LDataPoint(date.format(DateTimeFormatter.ofPattern("E, MMM dd hh:mm a")),  value)

        // STATS
        stats!!.value = getStats()
        }
        catch (e: Exception) {
            Log.i("Heart RateToException:", e.toString())
            // Run error handling here
        }
    }


    /** Health Connect data formatting for display on graph
     * @param start:  Date of start of daily values (between start and now)
     * @param response: response of aggregated data from health connect
     */
    private fun formatData(start: ZonedDateTime, response: List<AggregationResultGroupedByDuration>) {
        val listOfDates = createDateList(start)


        for (bucket in response) {
            val idDay = (0 until listOfDates.size).firstOrNull { listOfDates[it].dayOfYear == bucket.startTime.atZone(ZoneId.systemDefault()).dayOfYear }

            if( idDay != null ) {
                when (mName) {
                    "Steps" -> {
                        // Columns from 0 to the total number of steps
                        cFloatEntries_DailyMinMax[0][idDay] =
                            cFloatEntries_DailyMinMax[0][idDay].withY(bucket.result[StepsRecord.COUNT_TOTAL]!!.toFloat()) as FloatEntry
                    }

                    "Heart Rate" -> {
                        // Columns from min to max + average as point
                        cFloatEntries_DailyMinMax[0][idDay] =
                            cFloatEntries_DailyMinMax[0][idDay].withY(bucket.result[HeartRateRecord.BPM_MIN]!!.toFloat()) as FloatEntry
                        cFloatEntries_DailyMinMax[1][idDay] =
                            cFloatEntries_DailyMinMax[1][idDay].withY(bucket.result[HeartRateRecord.BPM_MAX]!!.toFloat()) as FloatEntry
                        cFloatEntries_DailyAvg[0][idDay] =
                            cFloatEntries_DailyAvg[0][idDay].withY(bucket.result[HeartRateRecord.BPM_AVG]!!.toFloat()) as FloatEntry
                    }

                    "Pressure" -> {
                        // Columns from min to max + average as point
                        if (bucket.result[BloodPressureRecord.DIASTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - bucket.result[BloodPressureRecord.DIASTOLIC_MIN]!!.inMillimetersOfMercury.toFloat() > 10f) {
                            cFloatEntries_DailyMinMax[0][idDay] =
                                cFloatEntries_DailyMinMax[0][idDay].withY(bucket.result[BloodPressureRecord.DIASTOLIC_MIN]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                            cFloatEntries_DailyMinMax[1][idDay] =
                                cFloatEntries_DailyMinMax[1][idDay].withY(bucket.result[BloodPressureRecord.DIASTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - cFloatEntries_DailyMinMax[0][idDay].y) as FloatEntry
                        }
                        if (bucket.result[BloodPressureRecord.SYSTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - bucket.result[BloodPressureRecord.SYSTOLIC_MIN]!!.inMillimetersOfMercury.toFloat() > 10f) {
                            cFloatEntries_DailyMinMax[0][idDay] =
                                cFloatEntries_DailyMinMax[2][idDay].withY(bucket.result[BloodPressureRecord.SYSTOLIC_MIN]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                            cFloatEntries_DailyMinMax[1][idDay] =
                                cFloatEntries_DailyMinMax[3][idDay].withY(bucket.result[BloodPressureRecord.SYSTOLIC_MAX]!!.inMillimetersOfMercury.toFloat() - cFloatEntries_DailyMinMax[0][idDay].y) as FloatEntry
                        }

                        cFloatEntries_DailyAvg[0][idDay] =
                            cFloatEntries_DailyAvg[0][idDay].withY(bucket.result[BloodPressureRecord.DIASTOLIC_AVG]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                        cFloatEntries_DailyAvg[1][idDay] =
                            cFloatEntries_DailyAvg[1][idDay].withY(bucket.result[BloodPressureRecord.SYSTOLIC_AVG]!!.inMillimetersOfMercury.toFloat()) as FloatEntry
                    }

                }
            }

        }
    }

    /** Calculate statistics about max, min, avg per day
     */
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
