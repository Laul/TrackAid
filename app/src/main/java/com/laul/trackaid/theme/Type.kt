package com.laul.trackaid.theme

import androidx.compose.material3.Typography

//
//val fontCustom = FontFamily(
//    Font(R.font.helveticaneue_bold, weight=  FontWeight.W800),
//    Font(R.font.helveticaneue_normal, weight=  FontWeight.W400),
//    Font(R.font.helveticaneue_light, weight= FontWeight.W200),
//    Font(R.font.helveticaneue_thin, weight= FontWeight.W100)
//)
//
//
//// Set of Material typography styles to start with
//val AppTypo = Typography(
//
//    h1 = TextStyle(
//        fontFamily = fontCustom,
//        fontWeight = FontWeight.W800,
//        fontSize = 24.sp
//    ),
//    h2 = TextStyle(
//        fontFamily = fontCustom,
//        fontWeight = FontWeight.W400,
//        fontSize = 20.sp
//    ),
//    h3 = TextStyle(
//        fontFamily = fontCustom,
//        fontWeight = FontWeight.W200,
//        fontSize = 16.sp
//    ),
//    body1 = TextStyle(
//        fontFamily = fontCustom,
//        fontWeight = FontWeight.W200,
//        fontSize = 14.sp
//    )
//
//
//)

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

//Replace with your font locations
val Roboto = FontFamily.Default


val AppTypography = Typography(
    labelLarge = TextStyle(
        fontFamily = Roboto ,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 16.sp,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 16.sp,
        fontSize = 11.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 24.sp,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 16.sp,
        fontSize = 12.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 40.sp,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 36.sp,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 32.sp,
        fontSize = 24.sp
    ),
    displayLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 64.sp,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 52.sp,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 44.sp,
        fontSize = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 28.sp,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 24.sp,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
)