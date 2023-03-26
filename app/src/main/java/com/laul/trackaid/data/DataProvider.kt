package com.laul.trackaid.data

import android.graphics.Color
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.laul.trackaid.LDataPoint
import com.laul.trackaid.R
import com.laul.trackaid.theme.color_surface_background
import com.laul.trackaid.views.NavRoutes


object DataProvider {

    val moduleList = mapOf(
        NavRoutes.Home.route to ModuleData(
            mId = 0,
            mName = "Home",
            mUnit =null,
            mIcon = R.drawable.ic_home,
            mIcon_outlined = R.drawable.ic_home_outline,
            mColor_Primary =  R.color.red_primary,
            mColor_Secondary = R.color.red_primary,
            gFitDataType = null,
            gFitOptions = null,
            lastDPoint = mutableStateOf( LDataPoint(0, 0, arrayListOf(0f))),
            duration = 7

        ),

        NavRoutes.Detailed.route + "/1" to ModuleData(
            mId = 1,
            mName = "Glucose",
            mUnit = "mmol/L",
            mIcon = R.drawable.ic_bg,
            mIcon_outlined = R.drawable.ic_bg_outline,
            mColor_Primary = Color.rgb(156,75,194),
            mColor_Secondary = Color.rgb(103,60,79),
            gFitDataType = HealthDataTypes.TYPE_BLOOD_GLUCOSE,
            gFitOptions = FitnessOptions.builder()
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
                .build(),
            lastDPoint = mutableStateOf( LDataPoint(0, 0, arrayListOf(0f))),
            duration = 7


        ),

        NavRoutes.Detailed.route + "/2" to ModuleData(
            mId = 2,
            mName = "Steps",
            mUnit = "steps",
            mIcon = R.drawable.ic_steps,
            mIcon_outlined = R.drawable.ic_steps_outline,
            mColor_Primary = Color.rgb(201,117,7),
            mColor_Secondary = Color.rgb(103,60,79),
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA,
            gFitOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build(),
            lastDPoint = mutableStateOf( LDataPoint(0, 0, arrayListOf(0f))),
            duration = 7
        ),

        NavRoutes.Detailed.route + "/3"  to ModuleData(
            mId = 3,
            mName = "Heart Rate",
            mUnit = "bpm",
            mIcon = R.drawable.ic_hr,
            mIcon_outlined = R.drawable.ic_hr_outline,
            mColor_Primary = Color.rgb(0,119,113),
            mColor_Secondary = Color.rgb(103,60,79),
            gFitDataType = DataType.TYPE_HEART_RATE_BPM,
            gFitOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .build(),
            lastDPoint = mutableStateOf( LDataPoint(0, 0, arrayListOf(0f))),
            duration = 7


        ),

        NavRoutes.Detailed.route + "/4" to ModuleData(
            mId = 4,
            mName = "Pressure",
            mUnit = "mmHg",
            mIcon = R.drawable.ic_bp,
            mIcon_outlined = R.drawable.ic_bp_outline,
            mColor_Primary = Color.rgb(55,138,215),
            mColor_Secondary = Color.rgb(103,60,79),
            gFitDataType = HealthDataTypes.TYPE_BLOOD_PRESSURE,
            gFitOptions = FitnessOptions.builder()
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .build(),
            lastDPoint = mutableStateOf( LDataPoint(0, 0, arrayListOf(0f, 0f))),
            duration = 7

        )
    )
}