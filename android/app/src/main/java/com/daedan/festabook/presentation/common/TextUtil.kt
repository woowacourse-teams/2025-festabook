package com.daedan.festabook.presentation.common

import java.time.LocalDate

fun formatFestivalPeriod(
    start: LocalDate,
    end: LocalDate,
): String =
    if (start.year == end.year) {
        "${start.year}. ${start.monthValue}. ${start.dayOfMonth} - " +
            "${end.monthValue}. ${end.dayOfMonth}"
    } else {
        "${start.year}. ${start.monthValue}. ${start.dayOfMonth} - " +
            "${end.year}. ${end.monthValue}. ${end.dayOfMonth}"
    }
