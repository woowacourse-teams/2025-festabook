package com.daedan.festabook.presentation.schedule.model

import com.daedan.festabook.domain.model.ScheduleDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ScheduleDateUiModel(
    val id: Long,
    val date: String,
)

fun ScheduleDate.toUiModel(): ScheduleDateUiModel =
    ScheduleDateUiModel(
        id = id,
        date = date.toFormattedDateString(),
    )

fun LocalDate.toFormattedDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("M/dd(E)", Locale.KOREAN)
    return this.format(formatter)
}
