package com.laul.trackaid.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.laul.trackaid.R


val fontCustom = FontFamily(
    Font(R.font.helveticaneue_bold, weight=  FontWeight.W800),
    Font(R.font.helveticaneue_normal, weight=  FontWeight.W400),
    Font(R.font.helveticaneue_light, weight= FontWeight.W200),
    Font(R.font.helveticaneue_thin, weight= FontWeight.W100)
)


// Set of Material typography styles to start with
val Typography = Typography(

    h1 = TextStyle(
        fontFamily = fontCustom,
        fontWeight = FontWeight.W800,
        fontSize = 16.sp
    ),
    h2 = TextStyle(
        fontFamily = fontCustom,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp
    ),
    h3 = TextStyle(
        fontFamily = fontCustom,
        fontWeight = FontWeight.W200,
        fontSize = 12.sp
    ),
    body1 = TextStyle(
        fontFamily = fontCustom,
        fontWeight = FontWeight.W200,
        fontSize = 12.sp
    )


)