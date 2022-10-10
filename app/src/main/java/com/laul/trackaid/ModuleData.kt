package com.laul.trackaid

import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat
import com.google.android.gms.fitness.data.DataType

data class ModuleData(
    val mId: Int,
    val mName: String,
    val mUnit: String,
    val mIcon: Int,
    val mColor_Primary: Int,
    val mColor_Secondary: Int,
    val gFitDataType: DataType
)