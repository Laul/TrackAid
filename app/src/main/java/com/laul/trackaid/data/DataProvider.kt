package com.laul.trackaid.data

import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.laul.trackaid.R
import com.laul.trackaid.views.NavRoutes


object DataProvider {

    val moduleList = mapOf(
        NavRoutes.Home.route to ModuleData(
            mId = 0,
            mName = "Home",
            mUnit =null,
            mIcon = R.drawable.ic_home,
            mIcon_outlined = R.drawable.ic_home_outline,
            mColor_Primary = null,
            mColor_Secondary = null,
            gFitDataType = null,
            gFitOptions = null
        ),

        NavRoutes.Detailed.route + "/1" to ModuleData(
            mId = 1,
            mName = "Glucose",
            mUnit = "mmol/L",
            mIcon = R.drawable.ic_bg,
            mIcon_outlined = R.drawable.ic_home_outline,
            mColor_Primary = R.color.red_primary,
            mColor_Secondary = R.color.red_secondary,
            gFitDataType = HealthDataTypes.TYPE_BLOOD_GLUCOSE,
            gFitOptions = FitnessOptions.builder()
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
                .build()
        ),

        NavRoutes.Detailed.route + "/2" to ModuleData(
            mId = 2,
            mName = "Steps",
            mUnit = "steps",
            mIcon = R.drawable.ic_steps,
            mIcon_outlined = R.drawable.ic_home_outline,
            mColor_Primary = R.color.orange_primary,
            mColor_Secondary = R.color.orange_secondary,
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA,
            gFitOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
                .build()
        ),

        NavRoutes.Detailed.route + "/3"  to ModuleData(
            mId = 3,
            mName = "Heart Rate",
            mUnit = "bpm",
            mIcon = R.drawable.ic_hr,
            mIcon_outlined = R.drawable.ic_home_outline,
            mColor_Primary = R.color.blue_primary,
            mColor_Secondary = R.color.blue_secondary,
            gFitDataType = DataType.TYPE_HEART_RATE_BPM,
            gFitOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .build()
        ),

        NavRoutes.Detailed.route + "/4" to ModuleData(
            mId = 4,
            mName = "Pressure",
            mUnit = "mmHg",
            mIcon = R.drawable.ic_bp,
            mIcon_outlined = R.drawable.ic_home_outline,
            mColor_Primary = R.color.pink_primary,
            mColor_Secondary = R.color.pink_secondary,
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA,
            gFitOptions = FitnessOptions.builder()
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .build()
        )
    )
}