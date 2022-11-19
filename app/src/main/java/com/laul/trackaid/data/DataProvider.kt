package com.laul.trackaid.data

import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.laul.trackaid.R

object DataProvider {

    val moduleList = listOf(
        ModuleData(
            mId = 1,
            mName = "Blood Glucose",
            mUnit = "mmol/L",
            mIcon = R.drawable.ic_bg,
            mColor_Primary = R.color.red_primary,
            mColor_Secondary = R.color.red_secondary,
            gFitDataType = HealthDataTypes.TYPE_BLOOD_GLUCOSE,
            gFitOptions = FitnessOptions.builder()
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_WRITE)
                .build()
    ),
        ModuleData(
            mId = 2,
            mName = "Steps",
            mUnit = "steps",
            mIcon = R.drawable.ic_steps,
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

        ModuleData(
            mId = 3,
            mName = "Heart Rate",
            mUnit = "bpm",
            mIcon = R.drawable.ic_hr,
            mColor_Primary = R.color.blue_primary,
            mColor_Secondary = R.color.blue_secondary,
            gFitDataType = DataType.TYPE_HEART_RATE_BPM,
            gFitOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .build()
        ),
        ModuleData(
            mId = 4,
            mName = "Blood Pressure",
            mUnit = "mmHg",
            mIcon = R.drawable.ic_bp,
            mColor_Primary = R.color.pink_primary,
            mColor_Secondary = R.color.pink_secondary,
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA,
            gFitOptions = FitnessOptions.builder()
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .build()
        )
    )
}