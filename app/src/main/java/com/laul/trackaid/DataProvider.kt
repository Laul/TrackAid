package com.laul.trackaid

import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes

object DataProvider {

    val moduleList = listOf(
        ModuleData(
            mId = 1,
            mName = "Blood Glucose",
            mUnit = "mmol/L",
            mIcon = R.drawable.icn_bg,
            mColor_Primary = R.color.red_primary,
            mColor_Secondary = R.color.red_secondary,
            gFitDataType = HealthDataTypes.TYPE_BLOOD_GLUCOSE
        ),
        ModuleData(
            mId = 1,
            mName = "Steps",
            mUnit = "steps",
            mIcon = R.drawable.icn_steps,
            mColor_Primary = R.color.orange_primary,
            mColor_Secondary = R.color.orange_secondary,
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
        ),
        ModuleData(
            mId = 1,
            mName = "Heart Rate",
            mUnit = "bpm",
            mIcon = R.drawable.icn_hr,
            mColor_Primary = R.color.blue_primary,
            mColor_Secondary = R.color.blue_secondary,
            gFitDataType = DataType.TYPE_HEART_RATE_BPM
        ),
        ModuleData(
            mId = 1,
            mName = "Blood Pressure",
            mUnit = "mmHg",
            mIcon = R.drawable.icn_bp,
            mColor_Primary = R.color.pink_primary,
            mColor_Secondary = R.color.pink_secondary,
            gFitDataType = DataType.TYPE_STEP_COUNT_DELTA
        )
    )
}