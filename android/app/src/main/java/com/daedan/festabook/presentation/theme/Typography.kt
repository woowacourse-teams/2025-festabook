package com.daedan.festabook.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.daedan.festabook.R

val FestabookTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_bold)),
                fontSize = 24.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_bold)),
                fontSize = 20.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_bold)),
                fontSize = 18.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                fontSize = 18.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                fontSize = 16.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_bold)),
                fontSize = 14.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                fontSize = 14.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_regular)),
                fontSize = 14.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_regular)),
                fontSize = 12.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_bold)),
                fontSize = 12.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                fontSize = 12.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily(Font(R.font.pretendard_regular)),
                fontSize = 10.sp,
            ),
    )
