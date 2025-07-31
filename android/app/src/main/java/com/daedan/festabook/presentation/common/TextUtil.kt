package com.daedan.festabook.presentation.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formatFestivalPeriod(
    start: String,
    end: String,
): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(start, inputFormatter)
    val endDate = LocalDate.parse(end, inputFormatter)

    return if (startDate.year == endDate.year) {
        "${startDate.year}년 ${startDate.monthValue}월 ${startDate.dayOfMonth}일 - " +
            "${endDate.monthValue}월 ${endDate.dayOfMonth}일"
    } else {
        "${startDate.year}년 ${startDate.monthValue}월 ${startDate.dayOfMonth}일 - " +
            "${endDate.year}년 ${endDate.monthValue}월 ${endDate.dayOfMonth}일"
    }
}
