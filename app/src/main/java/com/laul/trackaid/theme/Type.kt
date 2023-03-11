package com.laul.trackaid.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi


import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.laul.trackaid.R

//Replace with your font locations
//val Rajdhani = FontFamily(Font(R.font.helveticaneue_thin))


@OptIn(ExperimentalTextApi::class)
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

//
@OptIn(ExperimentalTextApi::class)
val fontName_Title = GoogleFont("Rajdhani")
@OptIn(ExperimentalTextApi::class)
val fontName_Body = GoogleFont("Barlow")

@OptIn(ExperimentalTextApi::class)
val FontFamily_Title = FontFamily(
    Font(googleFont = fontName_Title, fontProvider = provider)
)
@OptIn(ExperimentalTextApi::class)
val FontFamily_Body = FontFamily(
    Font(googleFont = fontName_Body, fontProvider = provider)
)


@OptIn(ExperimentalTextApi::class)
val AppTypography = Typography(
    labelLarge = TextStyle(
        fontFamily = FontFamily_Body ,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily_Body,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 8.sp,
        fontSize = 40.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily_Body,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 9.sp,
        fontSize = 11.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily_Body,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        lineHeight = 24.sp,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily_Body,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily_Body,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 16.sp,
        fontSize = 12.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
        lineHeight = 40.sp,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
        lineHeight = 36.sp,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
        lineHeight = 32.sp,
        fontSize = 24.sp
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.sp,
        lineHeight = 64.sp,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.sp,
        lineHeight = 52.sp,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.sp,
        lineHeight = 30.sp,
        fontSize = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
        lineHeight = 28.sp,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.15.sp,
        lineHeight = 30.sp,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily_Title,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
)