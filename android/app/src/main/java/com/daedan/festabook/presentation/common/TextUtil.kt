package com.daedan.festabook.presentation.common

import java.time.LocalDate

fun formatFestivalPeriod(
    start: LocalDate,
    end: LocalDate,
): String =
    if (start.year == end.year) {
        "${start.year}년 ${start.monthValue}월 ${start.dayOfMonth}일 ~ " +
            "${end.monthValue}월 ${end.dayOfMonth}일"
    } else {
        "${start.year}년 ${start.monthValue}월 ${start.dayOfMonth}일 ~ " +
            "${end.year}년 ${end.monthValue}월 ${end.dayOfMonth}일"
    }
