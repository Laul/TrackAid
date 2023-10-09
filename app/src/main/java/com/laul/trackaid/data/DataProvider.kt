package com.laul.trackaid.data

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.time.TimeRangeFilter
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.laul.trackaid.LDataPoint
import com.laul.trackaid.R
import com.laul.trackaid.views.NavRoutes
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


class DataProvider {
    companion object {

        val moduleList = mapOf(
            NavRoutes.Home.route to ModuleData(
                mId = 0,
                mName = "Home",
                mUnit = null,
                mIcon = R.drawable.ic_home,
                mIcon_outlined = R.drawable.ic_home_outline,
                mColor_Primary = R.color.red_primary,
                mColor_Secondary = R.color.red_primary,
                lastDPoint = mutableStateOf(LDataPoint(0, 0, arrayListOf(0f))),
                duration = 7,
                chartType = null,
                nCol = 0 ,
                nLines = 0,
                recordType = null
            ),
//
//            NavRoutes.Detailed.route + "/1" to ModuleData(
//                mId = 1,
//                mName = "Glucose",
//                mUnit = "mmol/L",
//                mIcon = R.drawable.ic_bg,
//                mIcon_outlined = R.drawable.ic_bg_outline,
//                mColor_Primary = Color.rgb(156, 75, 194),
//                mColor_Secondary = Color.rgb(103, 60, 79),
//                healthConnectDataType = TYPE_BLOOD_GLUCOSE,
//                gFitOptions = FitnessOptions.builder()
//                    .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
//                    .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
//                    .build(),
//                lastDPoint = mutableStateOf(LDataPoint(0, 0, arrayListOf(0f))),
//                duration = 7,
//                chartType = "Combo",
//                nCol = 2,
//                nLines= 1
//            ),

            NavRoutes.Detailed.route + "/2" to ModuleData(
                mId = 2,
                mName = "Steps",
                mUnit = "steps",
                mIcon = R.drawable.ic_steps,
                mIcon_outlined = R.drawable.ic_steps_outline,
                mColor_Primary = Color.rgb(201, 117, 7),
                mColor_Secondary = Color.rgb(103, 60, 79),
                lastDPoint = mutableStateOf(LDataPoint(0, 0, arrayListOf(0f))),
                duration = 7,
                chartType = "Columns",
                nCol = 1,
                nLines= 0,
                recordType = StepsRecord::class
            ),

//            NavRoutes.Detailed.route + "/3" to ModuleData(
//                mId = 3,
//                mName = "Heart Rate",
//                mUnit = "bpm",
//                mIcon = R.drawable.ic_hr,
//                mIcon_outlined = R.drawable.ic_hr_outline,
//                mColor_Primary = Color.rgb(0, 119, 113),
//                mColor_Secondary = Color.rgb(103, 60, 79),
//                lastDPoint = mutableStateOf(LDataPoint(0, 0, arrayListOf(0f))),
//                duration = 7,
//                chartType = "Combo",
//                nCol = 2,
//                nLines= 1,
//                recordType = HeartRateRecord::class
//            ),
//
//            NavRoutes.Detailed.route + "/4" to ModuleData(
//                mId = 4,
//                mName = "Pressure",
//                mUnit = "mmHg",
//                mIcon = R.drawable.ic_bp,
//                mIcon_outlined = R.drawable.ic_bp_outline,
//                mColor_Primary = Color.rgb(55, 138, 215),
//                mColor_Secondary = Color.rgb(103, 60, 79),
//                healthConnectDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE,
//                gFitOptions = FitnessOptions.builder()
//                    .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
//                    .build(),
//                lastDPoint = mutableStateOf(LDataPoint(0, 0, arrayListOf(0f, 0f))),
//                duration = 7,
//                chartType = "Columns",
//                nCol = 2,
//                nLines= 0
//            ),

//            NavRoutes.Detailed.route + "/5" to ModuleData(
//                mId = 4,
//                mName = "Weight",
//                mUnit = "kG",
//                mIcon = R.drawable.ic_weight,
//                mIcon_outlined = R.drawable.ic_weight_outline,
//                mColor_Primary = Color.rgb(22, 155, 0),
//                mColor_Secondary = Color.rgb(22, 155, 0),
//                gFitDataType = DataType.TYPE_WEIGHT,
//                gFitOptions = FitnessOptions.builder()
//                    .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
//                    .build(),
//                lastDPoint = mutableStateOf(LDataPoint(0, 0, arrayListOf(0f))),
//                duration = 7,
//                chartType = "Line",
//                nCol = 0,
//                nLines= 1
//            )
        )



        suspend fun healthConnectUpdate(client: HealthConnectClient){


            moduleList.values.toList().drop(1).forEach{

                // Create dates as Instants for HealthConnect
                val now = Instant.now()
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val start = startOfDay.toInstant().minus(it.duration.toLong(), ChronoUnit.DAYS)

                // Convert to localDateTime  for aggregation
                val nowLocal = LocalDateTime.ofInstant(now, java.time.ZoneId.systemDefault())
                val startLocal = LocalDateTime.ofInstant(start, java.time.ZoneId.systemDefault())

                // Clear all data before recomposition
                it.dPoints.clear()
                 it.getHealthConnectData(client, nowLocal, startLocal)
                it.aggregateStepsIntoDays(client,nowLocal, startLocal)

            }
        }
    }
}