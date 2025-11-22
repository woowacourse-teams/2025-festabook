package com.daedan.festabook.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.daedan.festabook.R

private val PretendardBold = FontFamily(Font(R.font.pretendard_bold))
private val PretendardMedium = FontFamily(Font(R.font.pretendard_medium))
private val PretendardRegular = FontFamily(Font(R.font.pretendard_regular))

val FestabookTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 24.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 20.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 18.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = PretendardMedium,
                fontSize = 18.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = PretendardMedium,
                fontSize = 16.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 14.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = PretendardMedium,
                fontSize = 14.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = PretendardRegular,
                fontSize = 14.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = PretendardRegular,
                fontSize = 12.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = PretendardBold,
                fontSize = 12.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = PretendardMedium,
                fontSize = 12.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = PretendardRegular,
                fontSize = 10.sp,
            ),
    )
